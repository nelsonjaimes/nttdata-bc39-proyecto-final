package com.nttdata.bc39.grupo04.customer.service;

import com.nttdata.bc39.grupo04.api.customer.CustomerDTO;
import com.nttdata.bc39.grupo04.api.customer.CustomerService;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.api.exceptions.NotFoundException;
import com.nttdata.bc39.grupo04.customer.persistence.CustomerEntity;
import com.nttdata.bc39.grupo04.customer.persistence.CustomerRepository;
import com.nttdata.bc39.grupo04.customer.redis.RedisConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.nttdata.bc39.grupo04.api.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;
    private final Logger logger = Logger.getLogger(CustomerServiceImpl.class);

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
                .map(mapper::entityToDto).collect(Collectors.toList());
    }

    @Cacheable(RedisConfiguration.CUSTOMER_CACHE)
    @Override
    public CustomerDTO getCustomerById(String customerId) {
        CustomerEntity entity = customerRepository.findByCode(customerId);
        if (Objects.isNull(entity)) {
            logger.debug("El cliente nro: " + customerId + ",no existe");
            throw new NotFoundException("Error, el cliente nro: " + customerId + ",no existe");
        }
        return mapper.entityToDto(entity);
    }

    @Override
    public void deleteCustomerById(String customerId) {
        CustomerEntity entity = customerRepository.findByCode(customerId);
        if (Objects.isNull(entity)) {
            logger.debug("Error, el cliente nro: " + customerId + ",no existe");
            throw new NotFoundException("Error, el cliente nro: " + customerId + ",no existe");
        }
        logger.debug("Cliente nro:" + customerId + " ,eliminado");
        customerRepository.deleteByCode(customerId);
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDto) {
        validationCreateCustomer(customerDto);
        CustomerEntity entity = mapper.dtoToEntity(customerDto);
        UUID uuid = UUID.randomUUID();
        entity.setId(uuid.toString());
        entity.setDate(Calendar.getInstance().getTime());
        try {
            return mapper.entityToDto(customerRepository.save(entity));
        } catch (DuplicateKeyException ex) {
            throw throwDuplicateCustomer(customerDto.getCode());
        }
    }

    private void validationCreateCustomer(CustomerDTO dto) {
        if (Objects.isNull(dto)) {
            throw new InvaliteInputException("Error, el formato de la peticion es invalido");
        }
        if (Objects.isNull(dto.getCode())) {
            throw new InvaliteInputException("Error, el codigo de cliente (code) es invalido");
        }
        if (Objects.isNull(dto.getType())) {
            throw new InvaliteInputException("Error, el tipo de cliente (type) es invalido");
        }
        if (!dto.getType().equals(CODE_ACCOUNT_EMPRESARIAL) && !dto.getType().equals(CODE_ACCOUNT_PERSONAL)) {
            throw new InvaliteInputException("Error, el tipo de cuenta invalido (accountType), verifique los datos admitidos: " + CODE_ACCOUNT_PERSONAL + " o " + CODE_ACCOUNT_EMPRESARIAL);
        }
        if (dto.getType().equals(CODE_ACCOUNT_PERSONAL) && dto.getCode().length() != LENGHT_CODE_PERSONAL_CUSTOMER) {
            throw new InvaliteInputException("Error, la longitud del codigo del cliente (code) es invalido, debe de ingresar el DNI de la persona.");
        }
        if (dto.getType().equals(CODE_ACCOUNT_EMPRESARIAL) && dto.getCode().length() != LENGHT_CODE_EMPRESARIAL_CUSTOMER) {
            throw new InvaliteInputException("Error, la longitud del codigo del cliente (code) es invalido, debe de ingresar el RUC de la empresa.");
        }
    }

    private RuntimeException throwDuplicateCustomer(String customerId) {
        logger.debug("Error, el cliente nro: " + customerId + ", ya esta registrado");
        return new InvaliteInputException("Error,ya existe un cliente con codigo: " + customerId);
    }

    @Override
    public CustomerDTO updateCustomerById(String customerId, CustomerDTO customerDto) {
        CustomerEntity entity = customerRepository.findByCode(customerId);
        if (Objects.isNull(entity)) {
            logger.debug("El cliente nro: " + customerId + ",no existe");
            throw new NotFoundException("Error, el cliente con codigo : " + customerId + " no existe");
        }
        entity.setName(customerDto.getName());
        logger.debug("cliente nro: " + customerId + ", actualizado correctamente, data=" + customerDto);
        return mapper.entityToDto(customerRepository.save(entity));
    }
}
