package com.nttdata.bc39.grupo04.composite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bc39.grupo04.api.account.*;
import com.nttdata.bc39.grupo04.api.credit.CreditDTO;
import com.nttdata.bc39.grupo04.api.credit.CreditService;
import com.nttdata.bc39.grupo04.api.customer.CustomerDTO;
import com.nttdata.bc39.grupo04.api.exceptions.BadRequestException;
import com.nttdata.bc39.grupo04.api.exceptions.HttpErrorInfo;
import com.nttdata.bc39.grupo04.api.exceptions.InvaliteInputException;
import com.nttdata.bc39.grupo04.api.exceptions.NotFoundException;
import com.nttdata.bc39.grupo04.api.movements.MovementsDTO;
import com.nttdata.bc39.grupo04.api.movements.MovementsReportDTO;
import com.nttdata.bc39.grupo04.api.movements.MovementsService;
import com.nttdata.bc39.grupo04.api.product.ProductDTO;
import com.nttdata.bc39.grupo04.api.product.ProductService;
import com.nttdata.bc39.grupo04.api.resttemplate.RestTemplateImpl;
import com.nttdata.bc39.grupo04.api.wallet.WalletDTO;
import com.nttdata.bc39.grupo04.api.wallet.WalletService;

import com.nttdata.bc39.grupo04.api.wallet.WalletAssociatedDTO;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.nttdata.bc39.grupo04.api.utils.Constants.*;

@Component
public class CompositeIntegration implements MovementsService, AccountService,
        ProductService, CreditService, WalletService {

    private final RestTemplate restTemplate;
    private static final Logger logger = Logger.getLogger(CompositeIntegration.class);
    private final String urlMovementsService;
    private final String urlAccountService;
    private final String urlCustomerService;
    private final String urlCreditService;

    private final String urlProductService;
    private final String urlWalletService;
    private final ObjectMapper mapper;

    public CompositeIntegration(RestTemplate restTemplate, ObjectMapper mapper, @Value("${app.movements-service.host}") String movementsServiceHost, @Value("${app.movements-service.port}") String movementsServicePort, @Value("${app.account-service.host}") String accountServiceHost, @Value("${app.account-service.port}") String accountServicePort, @Value("${app.customer-service.host}") String customerServiceHost, @Value("${app.customer-service.port}") String customerServicePort, @Value("${app.credit-service.host}") String creditServiceHost, @Value("${app.credit-service.port}") String creditServicePort, @Value("${app.product-service.host}") String productServiceHost, @Value("${app.product-service.port}") String productServicePort, @Value("${app.wallet-service.host}") String walletServiceHost, @Value("${app.wallet-service.port}") String walletServicePort) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.urlAccountService = String.format("http://%s:%s/%s", accountServiceHost, accountServicePort, "account");
        this.urlMovementsService = String.format("http://%s:%s/%s", movementsServiceHost, movementsServicePort, "movements");
        this.urlCustomerService = String.format("http://%s:%s/%s", customerServiceHost, customerServicePort, "customer");
        this.urlCreditService = String.format("http://%s:%s/%s", creditServiceHost, creditServicePort, "credit");
        this.urlProductService = String.format("http://%s:%s/%s", productServiceHost, productServicePort, "product");
        this.urlWalletService = String.format("http://%s:%s/%s", walletServiceHost, walletServicePort, "wallet");

        logger.debug("urlAccountService ====> " + urlAccountService);
        logger.debug("urlMovementsService ====> " + urlMovementsService);
        logger.debug("urlCustomerService ====> " + urlCustomerService);
        logger.debug("urlCreditService ====> " + urlCreditService);
        logger.debug("urlProductService ====> " + urlProductService);
        logger.debug("urlWalletService ====> " + urlWalletService);
    }

    // Movements
    @Override
    public Flux<MovementsReportDTO> getAllMovements() {
        String url = urlMovementsService + "/all";
        try {
            List<MovementsReportDTO> list = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<MovementsReportDTO>>() {
            }).getBody();
            if (Objects.isNull(list)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(list).flatMapMany(Flux::fromIterable);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getAllMovements:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<MovementsDTO> saveDepositMovement(MovementsDTO dto) {
        String url = urlMovementsService + "/deposit";
        try {
            MovementsDTO movementsDTO = restTemplate.postForObject(url, dto, MovementsDTO.class);
            if (Objects.isNull(movementsDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::saveDepositMovement: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<MovementsDTO> saveWithdrawlMovement(MovementsDTO dto) {
        String url = urlMovementsService + "/withdrawl";
        try {
            MovementsDTO movementsDTO = restTemplate.postForObject(url, dto, MovementsDTO.class);
            if (Objects.isNull(movementsDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::saveWithdrawlMovement " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Flux<MovementsReportDTO> getAllMovementsByNumberAccount(String accountNumber) {
        String url = urlMovementsService + "/" + accountNumber;
        try {
            List<MovementsReportDTO> list = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<MovementsReportDTO>>() {
            }).getBody();
            if (Objects.isNull(list)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Flux.fromStream(list.stream());
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getAllMovementsByNumberAccount:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<MovementsDTO> saveCreditMovement(MovementsDTO dto) {
        String url = urlMovementsService + "/credit";
        try {
            MovementsDTO movementsDTO = restTemplate.postForObject(url, dto, MovementsDTO.class);
            if (Objects.isNull(movementsDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::saveCreditMovement: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<MovementsDTO> savePaymentCreditCardMovement(MovementsDTO dto) {
        String url = urlMovementsService + "/paymentCreditCard";
        try {
            MovementsDTO movementsDTO = restTemplate.postForObject(url, dto, MovementsDTO.class);
            if (Objects.isNull(movementsDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::savePaymentCreditCardMovement: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<MovementsDTO> saveChargeCreditCardMovement(MovementsDTO dto) {
        String url = urlMovementsService + "/chargeCreditCard";
        try {
            MovementsDTO movementsDTO = restTemplate.postForObject(url, dto, MovementsDTO.class);
            if (Objects.isNull(movementsDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(movementsDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::saveChargeCreditMovement: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    // Acount
    @Override
    public Mono<DebitCardDTO> createDebitCard(DebitCardDTO debitCardDTO) {
        String url = urlAccountService + "/createDebitCard";
        try {
            DebitCardDTO dto = restTemplate.postForObject(url, debitCardDTO, DebitCardDTO.class);
            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::createDebitCard " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public void associateDebitCardWithWallet(WalletAssociatedDTO walletAssociatedDTO) {
        logger.warn("CompositeIntegration :::: associateDebitCardWithWallet: it's method is empty");
    }

    @Override
    public Flux<AccountDTO> getAllAccountByDebitCardNumber(String debitCardNumber) {
        String url = urlAccountService + "/debitCard/" + debitCardNumber;
        try {
            List<AccountDTO> list = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<AccountDTO>>() {
            }).getBody();
            if (Objects.isNull(list)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            Mono<List<AccountDTO>> monoList = Mono.just(list);
            return monoList.flatMapMany(Flux::fromIterable);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getAllAccountByCustomer:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<DebitCardNumberDTO> generateNumberDebitCard() {
        String url = urlAccountService + "/generateNumberDebitCard";
        RestTemplateImpl<DebitCardNumberDTO> restTemplateImpl = new RestTemplateImpl<>(mapper, restTemplate, logger);
        return restTemplateImpl.getWithReturnMono(url, DebitCardNumberDTO.class, "Got exception while make CompositeIntegration::generateNumberDebitCard: ");
    }

    @Override
    public Mono<AccountDTO> getByAccountNumber(String accountNumber) {
        String url = urlAccountService + "/" + accountNumber;
        try {
            AccountDTO accountDTO = restTemplate.getForObject(url, AccountDTO.class);
            if (Objects.isNull(accountDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(accountDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make getByAccountNumber " + ex.getMessage());
            if (accountNumber.equals(ACCOUNT_NUMBER_OF_ATM)) {
                AccountDTO atmAccount = new AccountDTO();
                atmAccount.setAccount(ACCOUNT_NUMBER_OF_ATM);
                atmAccount.setHolders(List.of(new HolderDTO("20100047218", "BANCO DE CREDITO DEL PERU")));
                atmAccount.setCustomerId("20100047218");
                atmAccount.setProductId(CODE_PRODUCT_CUENTA_CORRIENTE);
                atmAccount.setAvailableBalance(INITIAL_AMOUNT_OF_ATM);
                createAccount(atmAccount);
                return Mono.just(atmAccount);
            } else {
                throw handleHttpClientException(ex);
            }
        }
    }

    @Override
    public Flux<AccountDTO> getAllAccountByCustomer(String customerId) {
        String url = urlAccountService + "/customer/" + customerId;
        try {
            List<AccountDTO> list = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<AccountDTO>>() {
            }).getBody();
            if (Objects.isNull(list)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Flux.fromStream(list.stream());
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getAllAccountByCustomer:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<AccountDTO> createAccount(AccountDTO dto) {
        String url = urlAccountService + "/save";
        try {
            AccountDTO accountDTO = restTemplate.postForObject(url, dto, AccountDTO.class);
            if (Objects.isNull(accountDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(accountDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::createAccount: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<AccountDTO> makeDepositAccount(double amount, String accountNumber) {
        String url = urlAccountService + "/deposit/" + accountNumber + "?amount=" + amount;
        try {
            AccountDTO accountDTO = restTemplate.getForObject(url, AccountDTO.class);
            if (Objects.isNull(accountDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(accountDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::makeDepositAccount: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<AccountDTO> makeWithdrawalAccount(double amount, String accountNumber) {
        String url = urlAccountService + "/withdrawal/" + accountNumber + "?amount=" + amount;
        try {
            AccountDTO accountDTO = restTemplate.getForObject(url, AccountDTO.class);
            if (Objects.isNull(accountDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(accountDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::makeWithdrawalAccount: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<Void> deleteAccount(String accountNumber) {
        return null;
    }


    public Flux<CustomerDTO> getAllCustomers() {
        String url = urlCustomerService + "/all";
        try {
            List<CustomerDTO> list = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<CustomerDTO>>() {
            }).getBody();
            if (Objects.isNull(list)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Flux.fromStream(list.stream());
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getAllCustomers:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    public Mono<CustomerDTO> getCustomerById(String customerId) {
        String url = urlCustomerService + "/" + customerId;
        try {
            CustomerDTO dto = restTemplate.getForObject(url, CustomerDTO.class);
            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::getCustomerById: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    public Mono<Void> deleteCustomerById(String customerId) {
        return null;
    }

    public Mono<CustomerDTO> createCustomer(CustomerDTO customerDto) {
        String url = urlCustomerService + "/save";
        try {
            CustomerDTO dto = restTemplate.postForObject(url, customerDto, CustomerDTO.class);
            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::createCustomer: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    public Mono<CustomerDTO> updateCustomerById(String customerId, CustomerDTO customerDto) {
        return null;
    }

    // privates methods
    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException io) {
            return io.getMessage();
        }
    }

    // PRODUCTOS
    @Override
    public Flux<ProductDTO> getAllProducts() {
        String url = urlProductService + "/findAll";
        try {
            List<ProductDTO> list = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<ProductDTO>>() {
            }).getBody();

            if (Objects.isNull(list)) {
                throw new BadRequestException("Error, no se pudo establecer connexion con la url:" + url);
            }
            return Flux.fromStream(list.stream());
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getAllProducts:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<ProductDTO> getProductByCode(String code) {
        String url = urlProductService + "/findByCode/" + code;
        try {
            ProductDTO dto = restTemplate.getForObject(url, ProductDTO.class);
            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::getProductByCode: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<ProductDTO> createProduct(ProductDTO dto) {
        String url = urlProductService + "/save";
        try {
            ProductDTO productDTO = restTemplate.postForObject(url, dto, ProductDTO.class);
            if (Objects.isNull(productDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(productDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::createProduct: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<ProductDTO> updateProduct(ProductDTO dto) {
        String url = urlProductService + "/update/";
        try {
            restTemplate.put(url, ProductDTO.class);
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::updateProduct: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<Void> deleteProductByCode(String code) {
        String url = urlProductService + "/delete/" + code;
        try {
            restTemplate.delete(url);
            return Mono.empty();
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::deleteProductByCode: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    // Credit-service

    @Override
    public Mono<CreditDTO> createCredit(CreditDTO dto) {
        String url = urlCreditService + "/save";
        try {
            CreditDTO creditDTO = restTemplate.postForObject(url, dto, CreditDTO.class);
            if (Objects.isNull(creditDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(creditDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::createCredit: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<CreditDTO> getByCreditNumber(String creditNumber) {
        String url = urlCreditService + "/" + creditNumber;
        try {
            CreditDTO dto = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<CreditDTO>() {
            }).getBody();

            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer connexion con la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getByCreditNumber:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Flux<CreditDTO> getAllCreditByCustomer(String customerId) {
        String url = urlCreditService + "/customer/" + customerId;
        try {
            List<CreditDTO> list = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<CreditDTO>>() {
            }).getBody();

            if (Objects.isNull(list)) {
                throw new BadRequestException("Error, no se pudo establecer connexion con la url:" + url);
            }
            return Flux.fromStream(list.stream());
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getAllCreditByCustomer:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<CreditDTO> makePaymentCredit(double amount, String creditNumber) {
        String url = urlCreditService + "/paymentcredit/" + creditNumber + "?amount=" + amount;
        try {
            restTemplate.put(url, CreditDTO.class);
            CreditDTO dto = new CreditDTO();
            dto.setCreditNumber(creditNumber);
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::makePaymentCredit: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<CreditDTO> makePaymentCreditCard(double amount, String creditCardNumber) {
        String url = urlCreditService + "/paymentcreditcard/" + creditCardNumber + "?amount=" + amount;
        try {
            restTemplate.put(url, CreditDTO.class);
            CreditDTO dto = new CreditDTO();
            dto.setCardNumber(creditCardNumber);
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::makePaymentCreditCard: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<CreditDTO> makeChargeCredit(double amount, String creditCardNumber) {
        String url = urlCreditService + "/chargecreditcard/" + creditCardNumber + "?amount=" + amount;
        try {
            restTemplate.put(url, CreditDTO.class);
            CreditDTO dto = new CreditDTO();
            dto.setCardNumber(creditCardNumber);
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::makeChargeCredit: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Mono<Void> deleteCredit(String creditNumber) {
        String url = urlCreditService + "/" + creditNumber;
        try {
            restTemplate.delete(url);
            return Mono.empty();
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::deleteCredit: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    @Override
    public Flux<CreditDTO> getAllCreditCardByCustomer(String customerId) {
        String url = urlCreditService + "/getcreditcardcustomer/" + customerId;
        try {
            List<CreditDTO> list = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<CreditDTO>>() {
            }).getBody();

            if (Objects.isNull(list)) {
                throw new BadRequestException("Error, no se pudo establecer connexion con la url:" + url);
            }
            return Flux.fromStream(list.stream());
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getAllCreditByCustomer:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

    public RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {
            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(ex));
            case BAD_REQUEST:
                return new BadRequestException(getErrorMessage(ex));
            case UNPROCESSABLE_ENTITY:
                return new InvaliteInputException(getErrorMessage(ex));
            default:
                return ex;
        }
    }

    @Override
    public Mono<CreditDTO> getCreditCardByNumber(String creditCardNumber) {
        String url = urlCreditService + "/getCreditCardByNumber/" + creditCardNumber;
        try {
            CreditDTO dto = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<CreditDTO>() {
            }).getBody();

            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer connexion con la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while make CompositeIntegration::getByCreditNumber:  " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }
    
    //Wallet-Service
    
    @Override
    public Mono<WalletDTO> getWalletByPhoneNumber(String phoneNumber) {
        String url = urlWalletService + "/findByPhoneNumber/" + phoneNumber;
        try {
            WalletDTO dto = restTemplate.getForObject(url, WalletDTO.class);
            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::getWalletByPhoneNumber: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

	@Override
	public Flux<WalletDTO> getAllWallets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<WalletDTO> createWallet(WalletDTO dto) {
        String url = urlWalletService + "/save";
        try {
        	WalletDTO walletDTO = restTemplate.postForObject(url, dto, WalletDTO.class);
            if (Objects.isNull(walletDTO)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(walletDTO);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::createWallet: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
	}

	@Override
	public Mono<WalletDTO> updateWallet(WalletDTO dto) {
		return null;
	}

	@Override
	public Mono<Void> deleteWalletByPhoneNumber(String phoneNumber) {
		return null;
	}
	
    @Override
    public Mono<WalletDTO> makeDepositWallet(double amount, String numberPhone) {
        String url = urlWalletService + "/deposit/" + numberPhone + "?amount=" + amount;
        try {
        	WalletDTO dto = restTemplate.getForObject(url, WalletDTO.class);
            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::makeDepositWallet: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }

	@Override
	public Mono<WalletDTO> makeWithdrawalWallet(double amount, String numberPhone) {
        String url = urlWalletService + "/withdrawal/" + numberPhone + "?amount=" + amount;
        try {
        	WalletDTO dto = restTemplate.getForObject(url, WalletDTO.class);
            if (Objects.isNull(dto)) {
                throw new BadRequestException("Error, no se pudo establecer conexión con  la url:" + url);
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::makeWithdrawalWallet: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
	}
	
    @Override
    public Mono<WalletDTO> getWalletByDebitCardNumber(String debitCardNumber) {
        String url = urlWalletService + "/findByDebitCardNumber/" + debitCardNumber;
        try {
            WalletDTO dto = restTemplate.getForObject(url, WalletDTO.class);
            if (Objects.isNull(dto)) {
            	dto = new WalletDTO();
            }
            return Mono.just(dto);
        } catch (HttpClientErrorException ex) {
            logger.warn("Got exception while CompositeIntegration::getWalletByPhoneNumber: " + ex.getMessage());
            throw handleHttpClientException(ex);
        }
    }
}
