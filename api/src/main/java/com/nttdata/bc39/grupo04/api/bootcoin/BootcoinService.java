package com.nttdata.bc39.grupo04.api.bootcoin;

import java.util.List;

public interface BootcoinService {
    BootcoinDTO save(BootcoinDTO dto);

    BootcoinDTO getByDocumentNumber(String documentNumber);

    List<BootcoinDTO> getAllBootcoinAccounts();

    void deleteByDocumentNumber(String documentNumber);

    BootcoinOperationDTO requestBuyOperation(BootcoinOperationDTO body);

    BootcoinOperationDTO aceptSellOperation(BootcoinOperationDTO body);

    List<BootcoinOperationDTO> listOfAvailableOperations();
    List<BootcoinOperationDTO> getAllOperations();
}
