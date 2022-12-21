package com.nttdata.bc39.grupo04.customer.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, ObjectId> {

    CustomerEntity findByCode(String code);

    void deleteByCode(String code);
}
