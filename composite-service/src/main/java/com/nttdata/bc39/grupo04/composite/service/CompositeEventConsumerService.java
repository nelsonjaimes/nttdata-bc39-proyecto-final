package com.nttdata.bc39.grupo04.composite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.bc39.grupo04.api.composite.CompositeService;
import com.nttdata.bc39.grupo04.api.composite.TransactionTransferDTO;
import com.nttdata.bc39.grupo04.api.kafka.Event;
import com.nttdata.bc39.grupo04.api.kafka.EventType;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class CompositeEventConsumerService {
    private final CompositeService compositeService;
    private static final String topicBootcoin = "bootcoin";
    private static final Logger logger = Logger.getLogger(CompositeEventConsumerService.class);

    @KafkaListener(
            topics = topicBootcoin,
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "grupo1")
    public void consumer(Event<?> event) {
        if (!Objects.isNull(event)) {
            logger.debug("CompositeEventService:::consumer :" + event);
            if (event.getType() == EventType.MAKE_TRANSFERENCE) {
                Logger.getLogger("MAKE_TRANSFERENCE  message=>" + event.getMessage());
                Logger.getLogger("MAKE_TRANSFERENCE  data=>" + event.getData());
                ObjectMapper mapper = new ObjectMapper();
                TransactionTransferDTO transactionTransferDTO = mapper.convertValue(event.getData(),
                        TransactionTransferDTO.class);
                compositeService.makeTransferAccount(transactionTransferDTO);
            }
        }

    }

}
