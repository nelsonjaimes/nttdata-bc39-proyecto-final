package com.nttdata.bc39.grupo04.wallet.service;

import static com.nttdata.bc39.grupo04.api.utils.Constants.MAX_DEPOSIT_AMOUNT;
import static com.nttdata.bc39.grupo04.api.utils.Constants.MAX_WITHDRAWAL_AMOUNT;
import static com.nttdata.bc39.grupo04.api.utils.Constants.MIN_DEPOSIT_AMOUNT;
import static com.nttdata.bc39.grupo04.api.utils.Constants.MIN_WITHDRAWAL_AMOUNT;
import static com.nttdata.bc39.grupo04.api.utils.Constants.MIN_DEPOSIT_WALLET;
import static com.nttdata.bc39.grupo04.api.utils.Constants.MAX_DEPOSIT_WALLET;
import static com.nttdata.bc39.grupo04.api.utils.Constants.MAX_WITHDRAWAL_WALLET;
import static com.nttdata.bc39.grupo04.api.utils.Constants.MIN_WITHDRAWAL_WALLET;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.nttdata.bc39.grupo04.api.exceptions.BadRequestException;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.api.exceptions.NotFoundException;
import com.nttdata.bc39.grupo04.api.utils.Constants;
import com.nttdata.bc39.grupo04.api.wallet.WalletDTO;
import com.nttdata.bc39.grupo04.api.wallet.WalletService;
import com.nttdata.bc39.grupo04.wallet.persistence.WalletEntity;
import com.nttdata.bc39.grupo04.wallet.persistence.WalletRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WalletServiceImpl implements WalletService {

	private WalletRepository repository;
	private WalletMapper mapper;
	private static final Logger LOG = Logger.getLogger(WalletServiceImpl.class);

	@Autowired
	public WalletServiceImpl(WalletRepository repository, WalletMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	@Override
	public Flux<WalletDTO> getAllWallets() {
		return repository.findAll().map(mapper::entityToDto);
	}

	@Override
	public Mono<WalletDTO> getWalletByPhoneNumber(String phoneNumber) {
		if (ObjectUtils.isEmpty(phoneNumber)) {
			LOG.info("Error, El Wallet que intenta consultar es inválido");
			throw new InvaliteInputException("Error, El Wallet que intenta consultar es invalido");
		}
		Mono<WalletDTO> dto = repository.findByPhoneNumber(phoneNumber).map(mapper::entityToDto);
		return dto;
	}

	@Override
	public Mono<WalletDTO> createWallet(WalletDTO dto) {
		Mono<WalletEntity> entity = repository.findByPhoneNumber(dto.getPhoneNumber());
		WalletEntity entityNew = entity.block();
		if (!ObjectUtils.isEmpty(entityNew)) {
			throw new InvaliteInputException("Error, El Wallet que intenta crear ya está registrado");
		}

		dto.setCreateDate(Calendar.getInstance().getTime());
		dto.setProductId(Constants.CODE_PRODUCT_WALLET);
		entityNew = mapper.dtoToEntity(dto);
		return repository.save(entityNew).map(mapper::entityToDto);
	}

	@Override
	public Mono<WalletDTO> updateWallet(WalletDTO dto) {
		Mono<WalletEntity> entity = repository.findByPhoneNumber(dto.getPhoneNumber());
		WalletEntity entityNew = entity.block();

		if (ObjectUtils.isEmpty(entityNew)) {
			LOG.info("Error, El Wallet que intenta modificar no existe");
			throw new NotFoundException("Error, El Wallet que intenta modificar no existe");
		}
		entityNew.setEmail(dto.getEmail());
		entityNew.setImei(dto.getImei());
		entityNew.setDebitCardNumber(dto.getDebitCardNumber());
		entityNew.setModifyDate(Calendar.getInstance().getTime());
		return repository.save(entityNew).map(mapper::entityToDto);
	}

	@Override
	public Mono<Void> deleteWalletByPhoneNumber(String phoneNumber) {
		Mono<WalletEntity> entity = repository.findByPhoneNumber(phoneNumber);
		WalletEntity newEntity = entity.block();
		if (ObjectUtils.isEmpty(newEntity)) {
			LOG.info("Error, El Wallet que intenta eliminar no existe");
			throw new NotFoundException("Error, El Wallet que intenta eliminar no existe");
		}
		return repository.delete(newEntity);
	}

	@Override
	public Mono<WalletDTO> makeDepositWallet(double amount, String numberPhone) {
		WalletEntity entity = repository.findByPhoneNumber(numberPhone).block();
		if (Objects.isNull(entity)) {
			LOG.debug("Error, no existe la billetera con Nro: " + numberPhone);
			throw new NotFoundException("Error, no existe la billetera con Nro: " + numberPhone);
		}
		if (amount < MIN_DEPOSIT_WALLET || amount > MAX_DEPOSIT_WALLET) {
			LOG.debug("Error limites de deposito ,billetera: " + numberPhone + " con monto: " + amount);
			throw new NotFoundException(
					String.format(Locale.getDefault(), "Error, los limites de DEPOSITO son min: %d sol y max: %d sol",
							MIN_DEPOSIT_WALLET, MAX_DEPOSIT_WALLET));
		}
		double newAvailableBalance = entity.getAvailableBalance() + amount;
		entity.setAvailableBalance(newAvailableBalance);
		entity.setModifyDate(Calendar.getInstance().getTime());
		return repository.save(entity).map(mapper::entityToDto);
	}

	@Override
	public Mono<WalletDTO> makeWithdrawalWallet(double amount, String numberPhone) {
		WalletEntity entity = repository.findByPhoneNumber(numberPhone).block();
		if (Objects.isNull(entity)) {
			LOG.debug("Error, no existe la billetera con Nro: " + numberPhone);
			throw new NotFoundException("Error, no existe la billetera con Nro: " + numberPhone);
		}
		if (amount < MIN_WITHDRAWAL_WALLET || amount > MAX_WITHDRAWAL_WALLET) {
			LOG.debug("El retirno, no cumple con los limites establecidos , Nro : " + numberPhone);
			throw new NotFoundException(
					String.format(Locale.getDefault(), "Error, los limites de RETIRO son min: %d sol y max: %d sol",
							MIN_WITHDRAWAL_WALLET, MAX_WITHDRAWAL_WALLET));
		}
		double availableBalance = entity.getAvailableBalance();
		if (amount > availableBalance) {
			LOG.debug("Saldo insuficiente, billetera con Nro:" + numberPhone);
			throw new BadRequestException("Error,saldo insuficiente en billetera Nro: " + numberPhone);
		}
		availableBalance -= amount;
		entity.setAvailableBalance(availableBalance);
		entity.setModifyDate(new Date());
		return repository.save(entity).map(mapper::entityToDto);
	}

	@Override
	public Mono<WalletDTO> getWalletByDebitCardNumber(String debitCardNumber) {
		if (ObjectUtils.isEmpty(debitCardNumber)) {
			LOG.info("Error, La tarjeta asociada al wallet que intenta consultar es inválido");
			throw new InvaliteInputException("Error, debitCardNumber");
		}
		Mono<WalletDTO> dto = repository.findByDebitCardNumber(debitCardNumber).map(mapper::entityToDto);
		return dto;
	}
}
