package com.nttdata.bc39.grupo04.bootcoin.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BootcoinRepository extends CrudRepository<BootcoinEntity, String> {
    BootcoinEntity findByDocumentNumber(String documentNumber);

    void deleteByDocumentNumber(String documentNumber);
}
