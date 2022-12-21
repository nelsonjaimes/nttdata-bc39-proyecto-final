package com.nttdata.bc39.grupo04.composite.service;

import com.nttdata.bc39.grupo04.api.kafka.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompositeEventService {
    private final KafkaTemplate<String, Event<?>> producer;
    private static final String topicAccount = "accounts";

    public void publish(Event<?> event) {
        producer.send(topicAccount, event);
    }

}
