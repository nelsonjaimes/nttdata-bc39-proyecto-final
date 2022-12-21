package com.nttdata.bc39.grupo04.bootcoin.service;

import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinDTO;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinEntity;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Service
public class BootcoinServiceImpl implements BootcoinService {

    private final BootcoinMapper mapper;
    private final BootcoinRepository bootcoinRepository;

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

    @Override
    public BootcoinDTO getByDocumentNumber(String documentNumber) {
        BootcoinEntity entity = bootcoinRepository.findByDocumentNumber(documentNumber);
        return mapper.entityToDto(entity);
    }

    @Override
    public List<BootcoinDTO> getAllAccounts() {
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
