package com.nttdata.bc39.grupo04.composite.service;

import com.nttdata.bc39.grupo04.api.kafka.Event;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("all")
@RequiredArgsConstructor
public class CompositeEventService {
    private final KafkaTemplate<String, Event<?>> producer;
    private static final String topicAccount = "accounts";
    private static final Logger logger = Logger.getLogger(CompositeEventService.class);

    public void publish(Event<?> event) {
        logger.debug("CompositeEventService::publish ==> " + event);
        producer.send(topicAccount, event);
    }
}
