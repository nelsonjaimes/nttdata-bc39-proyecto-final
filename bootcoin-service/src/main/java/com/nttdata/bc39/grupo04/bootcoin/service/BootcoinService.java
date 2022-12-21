package com.nttdata.bc39.grupo04.bootcoin.service;

import com.nttdata.bc39.grupo04.api.bootcoin.BootcoinDTO;

import java.util.List;

public interface BootcoinService {
    BootcoinDTO save(BootcoinDTO dto);

    BootcoinDTO getByDocumentNumber(String documentNumber);

    List<BootcoinDTO> getAllAccounts();

    void deleteByDocumentNumber(String documentNumber);
}
