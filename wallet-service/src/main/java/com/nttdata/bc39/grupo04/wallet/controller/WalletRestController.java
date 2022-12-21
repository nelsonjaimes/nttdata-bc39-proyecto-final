package com.nttdata.bc39.grupo04.wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.bc39.grupo04.api.account.AccountDTO;
import com.nttdata.bc39.grupo04.api.wallet.WalletDTO;
import com.nttdata.bc39.grupo04.api.wallet.WalletService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/wallet")
public class WalletRestController {

	@Autowired
	private WalletService service;

	@GetMapping(value = "/findAll")
	public Flux<WalletDTO> getAllWallets() {
		return service.getAllWallets();
	}

	@GetMapping(value = "/findByPhoneNumber/{phoneNumber}")
	public Mono<WalletDTO> getWalletByPhoneNumber(@PathVariable(value = "phoneNumber") String phoneNumber) {
		return service.getWalletByPhoneNumber(phoneNumber);
	}
	
	@GetMapping(value = "/findByDebitCardNumber/{debitCardNumber}")
	public Mono<WalletDTO> getWalletByDebitCardNumber(@PathVariable(value = "debitCardNumber") String debitCardNumber) {
		return service.getWalletByDebitCardNumber(debitCardNumber);
	}

	@PostMapping(value = "/save")
	public Mono<WalletDTO> createWallet(@RequestBody WalletDTO body) {
		WalletDTO.of(body);
		return service.createWallet(body);
	}

	@PutMapping(value = "/update")
	public Mono<WalletDTO> updateWallet(@RequestBody WalletDTO body) {
		return service.updateWallet(body);
	}

	@DeleteMapping(value = "/delete/{phoneNumber}")
	public Mono<Void> deleteWalletByPhoneNumber(@PathVariable(value = "phoneNumber") String phoneNumber) {
		return service.deleteWalletByPhoneNumber(phoneNumber);
	}

	@GetMapping("/deposit/{numberPhone}")
	Mono<WalletDTO> makeDeposit(@PathVariable(value = "numberPhone") String numberPhone,
			@RequestParam(value = "amount") double amount) {
		return service.makeDepositWallet(amount, numberPhone);
	}
	
    @GetMapping("/withdrawal/{numberPhone}")
    Mono<WalletDTO> makeWithdrawal(@PathVariable(value = "numberPhone") String numberPhone, @RequestParam(value = "amount") double amount) {
        return service.makeWithdrawalWallet(amount, numberPhone);
    }

}
