package com.nttdata.bc39.grupo04.bootcoin.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootcoinOperationRepository extends CrudRepository<BootcoinOperationEntity, String> {
    BootcoinOperationEntity findByRequestNumber(String requestNumber);
}
