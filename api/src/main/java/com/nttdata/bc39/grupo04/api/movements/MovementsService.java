package com.nttdata.bc39.grupo04.api.movements;

import com.nttdata.bc39.grupo04.api.utils.CodesEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementsService {

    Mono<MovementsDTO> saveDepositMovement(MovementsDTO dto);

    Mono<MovementsDTO> saveWithdrawlMovement(MovementsDTO dto);

    Flux<MovementsReportDTO> getAllMovementsByNumberAccount(String accountNumber);

    Flux<MovementsReportDTO> getAllMovements();
    
    Mono<MovementsDTO> saveCreditMovement(MovementsDTO dto);
    
    Mono<MovementsDTO> savePaymentCreditCardMovement(MovementsDTO dto);
    
    Mono<MovementsDTO> saveChargeCreditCardMovement(MovementsDTO dto);
}
