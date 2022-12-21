package com.nttdata.bc39.grupo04.bootcoin.service;

import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinDTO;
import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinOperationDTO;
import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinService;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.api.utils.Constants;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinEntity;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinOperationEntity;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinOperationRepository;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinRepository;
import com.nttdata.bc39.grupo04.bootcoin.redis.RedisConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class BootcoinServiceImpl implements BootcoinService {

    private final BootcoinMapper mapper;
    private final BootcoinRepository bootcoinRepository;
    private final BootcoinOperationRepository bootcoinOperationRepository;

    @Override
    public BootcoinOperationDTO aceptSellOperation(BootcoinOperationDTO body) {
        Supplier<String> transactionSupplier = () -> String.valueOf(System.currentTimeMillis());
        BootcoinOperationEntity entity = bootcoinOperationRepository.findByRequestNumber(body.getRequestNumber());
        if (Objects.isNull(entity)) {
            throw new InvaliteInputException("Error, no existe la solicitud de compra  id:" + body.getRequestNumber());
        }
        entity.setSellerNumber(body.getSellerNumber());
        entity.setSellerAcceptanceDate(new Date());
        entity.setSellerAccountNumber(body.getSellerAccountNumber());
        entity.setSellerNumberWallet(body.getSellerNumberWallet());
        entity.setTransactionNumber(transactionSupplier.get());
        entity.setTotalAmountPen(entity.getAmountCoins() * Constants.BOOTCOIN_SELL_RATE);
        BootcoinOperationEntity updateEntity = bootcoinOperationRepository.save(entity);
        return mapper.operationEntityToDto(updateEntity);
    }

    @Override
    public BootcoinOperationDTO requestBuyOperation(BootcoinOperationDTO body) {
        BootcoinOperationEntity entity = mapper.operationDtoToEntity(body);
        entity.setBuyerRequestDate(new Date());
        Supplier<String> requestIdSupplier = () -> UUID.randomUUID().toString();
        entity.setRequestNumber(requestIdSupplier.get());
        return mapper.operationEntityToDto(bootcoinOperationRepository.save(entity));
    }

    @Override
    public List<BootcoinOperationDTO> listOfAvailableOperations() {
        return StreamSupport.stream(bootcoinOperationRepository.findAll().spliterator(), false)
                .filter(opera -> Objects.isNull(opera.getSellerNumber()))
                .map(mapper::operationEntityToDto).collect(Collectors.toList());
    }

    @Override
    public List<BootcoinOperationDTO> getAllOperations() {
        return StreamSupport.stream(bootcoinOperationRepository.findAll().spliterator(), false)
                .map(mapper::operationEntityToDto).collect(Collectors.toList());
    }

    @Override
    public BootcoinDTO save(BootcoinDTO dto) {
        validationCreateBootcoinAccount(dto);
        BootcoinEntity entity = mapper.dtoToEntity(dto);
        UUID uuid = UUID.randomUUID();
        entity.setId(uuid.toString());
        try {
            return mapper.entityToDto(bootcoinRepository.save(entity));
        } catch (DuplicateKeyException exception) {
            throw new InvaliteInputException("Error, ya existe una cuenta con el numero de documento: "
                    + dto.getDocumentNumber());
        }
    }

    void validationCreateBootcoinAccount(BootcoinDTO dto) {
        if (Objects.isNull(dto)) {
            throw new InvaliteInputException("Erorr, el cuerpo de la solicitud es invalido");
        }
        if (Objects.isNull(dto.getDocumentNumber())) {
            throw new InvaliteInputException("Error, numero de documento invalido");
        }
        if (Objects.isNull(dto.getDocumentType())) {
            throw new InvaliteInputException("Error, tipo de documento invalido");
        }
        if (Objects.isNull(dto.getPhoneNumber())) {
            throw new InvaliteInputException("Error, numero de telefono invalido");
        }
        if (Objects.isNull(dto.getMail())) {
            throw new InvaliteInputException("Error, mail invalido");
        }
    }

    @Cacheable(RedisConfiguration.BOOTCOIN_CACHE)
    @Override
    public BootcoinDTO getByDocumentNumber(String documentNumber) {
        BootcoinEntity entity = bootcoinRepository.findByDocumentNumber(documentNumber);
        return mapper.entityToDto(entity);
    }

    @Override
    public List<BootcoinDTO> getAllBootcoinAccounts() {
        return StreamSupport.stream(bootcoinRepository.findAll().spliterator(), false)
                .map(mapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteByDocumentNumber(String documentNumber) {
        if (Objects.isNull(documentNumber)) {
            throw new InvaliteInputException("Error , el numero de documento es invalido");
        }
        bootcoinRepository.deleteByDocumentNumber(documentNumber);
    }
}
