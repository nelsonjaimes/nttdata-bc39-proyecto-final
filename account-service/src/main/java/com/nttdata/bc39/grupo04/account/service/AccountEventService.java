package com.nttdata.bc39.grupo04.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bc39.grupo04.api.kafka.Event;
import com.nttdata.bc39.grupo04.api.wallet.WalletAssociatedDTO;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@SuppressWarnings("all")
@Component
@RequiredArgsConstructor
public class AccountEventService {
    private static final Logger logger = Logger.getLogger(AccountEventService.class);
    private static final String topicAccount = "accounts";
    private final AccountServiceImpl accountService;

    @KafkaListener(
            topics = topicAccount,
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumer(Event<?> event) {
        if (!Objects.isNull(event)) {
            logger.debug("AccountEventService:::consumer :" + event.toString());
            switch (event.getType()) {
                case REQUEST_ACCOUNT_BY_ID:
                    logger.debug("No podemos enviar los solicitado...");
                    break;
                case ASSOCIATED_WITH_WALLET:
                    logger.getLogger("ASSOCIATED_WITH_WALLET  message=>" + event.getMessage());
                    logger.getLogger("ASSOCIATED_WITH_WALLET  data=>" + event.getData());
                    ObjectMapper mapper = new ObjectMapper();
                    WalletAssociatedDTO walletAssociatedDTO = mapper.convertValue(event.getData(),
                            WalletAssociatedDTO.class);
                    accountService.associateDebitCardWithWallet(walletAssociatedDTO);
                    break;
            }
        }

    }
}
