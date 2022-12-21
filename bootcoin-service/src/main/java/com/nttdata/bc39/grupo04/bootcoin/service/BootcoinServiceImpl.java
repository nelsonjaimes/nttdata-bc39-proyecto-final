package com.nttdata.bc39.grupo04.bootcoin.service;

import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinDTO;
import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinOperationDTO;
import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinService;
import com.nttdata.bc39.grupo04.api.composite.TransactionTransferDTO;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.api.exceptions.NotFoundException;
import com.nttdata.bc39.grupo04.api.kafka.Event;
import com.nttdata.bc39.grupo04.api.kafka.EventType;
import com.nttdata.bc39.grupo04.api.utils.Constants;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinEntity;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinOperationEntity;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinOperationRepository;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinRepository;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
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

import static com.nttdata.bc39.grupo04.bootcoin.redis.RedisConfiguration.BOOTCOIN_CACHE;

@RequiredArgsConstructor
@Service
public class BootcoinServiceImpl implements BootcoinService {

    private final BootcoinMapper mapper;
    private final BootcoinRepository bootcoinRepository;
    private final BootcoinOperationRepository bootcoinOperationRepository;
    private static final Logger logger = Logger.getLogger(BootcoinServiceImpl.class);
    private final BootcoinEventService bootcoinEventService;

    @Override
    public BootcoinOperationDTO aceptSellOperation(BootcoinOperationDTO body) {
        Supplier<String> transactionSupplier = () -> String.valueOf(System.currentTimeMillis());
        BootcoinOperationEntity entity = bootcoinOperationRepository.findByRequestNumber(body.getRequestNumber());
        if (Objects.isNull(entity)) {
            throw new InvaliteInputException("Error, no existe la solicitud de compra  id:" + body.getRequestNumber());
        }

        double totalAmount = entity.getAmountCoins() * Constants.BOOTCOIN_SELL_RATE;
        //make transference
        TransactionTransferDTO transferDTO = new TransactionTransferDTO();
        transferDTO.setSourceAccount(entity.getBuyerAccountNumber());
        transferDTO.setDestinationAccount(body.getSellerAccountNumber());
        transferDTO.setAmount(totalAmount);

        Event<TransactionTransferDTO> transactionTransferDTOEvent = new Event<>(transferDTO,
                EventType.MAKE_TRANSFERENCE, "Enviando transferencia desde bootcoin-service");
        logger.debug("MAKE_TRANSFERENCE:::" + transactionTransferDTOEvent);
        bootcoinEventService.publish(transactionTransferDTOEvent);
        //update in bootcoin
        entity.setSellerNumber(body.getSellerNumber());
        entity.setSellerAcceptanceDate(new Date());
        entity.setSellerAccountNumber(body.getSellerAccountNumber());
        entity.setSellerNumberWallet(body.getSellerNumberWallet());
        entity.setTransactionNumber(transactionSupplier.get());
        entity.setTotalAmountPen(totalAmount);
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
        entity.setCreatedDate(new Date());
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

    @Cacheable(BOOTCOIN_CACHE)
    @Override
    public BootcoinDTO getByDocumentNumber(String documentNumber) {
        BootcoinEntity entity = bootcoinRepository.findByDocumentNumber(documentNumber);
        if (Objects.isNull(entity)) {
            logger.debug("El cliente nro: " + documentNumber + ",no existe");
            throw new NotFoundException("Error, el cliente nro: " + documentNumber + ",no existe");
        }
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
