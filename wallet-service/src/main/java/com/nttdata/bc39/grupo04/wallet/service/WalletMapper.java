package com.nttdata.bc39.grupo04.wallet.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.nttdata.bc39.grupo04.api.wallet.WalletDTO;
import com.nttdata.bc39.grupo04.wallet.persistence.WalletEntity;


@Mapper(componentModel = "spring")
public interface WalletMapper {

    WalletDTO entityToDto(WalletEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    WalletEntity dtoToEntity(WalletDTO dto);
}

