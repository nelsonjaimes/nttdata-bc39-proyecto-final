package com.nttdata.bc39.grupo04.wallet.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;

@Repository
public interface WalletRepository extends ReactiveCrudRepository<WalletEntity, ObjectId>{

	Mono<WalletEntity> findByPhoneNumber(String phoneNumber);
	Mono<WalletEntity> findByDebitCardNumber(String debitCardNumber);
}
