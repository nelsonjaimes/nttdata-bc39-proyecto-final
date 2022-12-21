package com.nttdata.bc39.grupo04.api.wallet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletService {
    Flux<WalletDTO> getAllWallets();

    Mono<WalletDTO> getWalletByPhoneNumber(String phoneNumber);

    Mono<WalletDTO> createWallet(WalletDTO dto);

    Mono<WalletDTO> updateWallet(WalletDTO dto);

    Mono<Void> deleteWalletByPhoneNumber(String phoneNumber);
    
    Mono<WalletDTO> makeDepositWallet(double amount, String numberPhone);
    
    Mono<WalletDTO> makeWithdrawalWallet(double amount, String numberPhone);
    
    Mono<WalletDTO> getWalletByDebitCardNumber(String debitCardNumber);
}


