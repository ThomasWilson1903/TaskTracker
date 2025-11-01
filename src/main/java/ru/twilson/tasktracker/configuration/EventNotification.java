package ru.twilson.tasktracker.configuration;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventNotification extends ApplicationEvent {

    private final String id;
    private final String title;

    public EventNotification(Object source, String id, String title) {
        super(source);
        this.id = id;
        this.title = title;
    }
}
