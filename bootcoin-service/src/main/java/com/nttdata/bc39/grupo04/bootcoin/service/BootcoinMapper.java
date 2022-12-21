package com.nttdata.bc39.grupo04.bootcoin.service;


import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinDTO;
import com.nttdata.bc39.grupo04.bootcoin.persistence.BootcoinEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface BootcoinMapper {
    BootcoinDTO entityToDto(BootcoinEntity entity);

    @Mappings({@Mapping(target = "id", ignore = true)})
    BootcoinEntity dtoToEntity(BootcoinDTO dto);
}
