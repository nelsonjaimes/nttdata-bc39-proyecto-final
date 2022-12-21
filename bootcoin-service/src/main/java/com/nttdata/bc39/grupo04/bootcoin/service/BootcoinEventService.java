package com.nttdata.bc39.grupo04.bootcoin.service;

import com.nttdata.bc39.grupo04.api.kafka.Event;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class BootcoinEventService {
    private final KafkaTemplate<String, Event<?>> producer;
    ;
    private static final String topicBootcoin = "bootcoin";
    private static final Logger logger = Logger.getLogger(BootcoinEventService.class);

    public void publish(Event<?> event) {
        logger.debug("BootcoinEventService::publish =>" + event);
        producer.send(topicBootcoin, event);
    }
}
