package com.nttdata.bc39.grupo04.api.kafka;

import lombok.*;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Event<T> {
    private String key;
    private Date date;
    private T data;
    private EventType type;
    private String message;

    public Event(T data, EventType type, String message) {
        this.data = data;
        this.type = type;
        this.message = message;
        this.key = UUID.randomUUID().toString();
        this.date = Calendar.getInstance().getTime();
    }

    @Override
    public String toString() {
        return "Event{" +
                "key='" + key + '\'' +
                ", date=" + date +
                ", data=" + data +
                ", type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
