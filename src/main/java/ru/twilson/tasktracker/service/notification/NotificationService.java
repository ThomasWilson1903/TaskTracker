package ru.twilson.tasktracker.service.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.twilson.tasktracker.configuration.EventNotification;

import static org.springframework.amqp.core.MessageProperties.CONTENT_TYPE_JSON;
import static ru.twilson.tasktracker.configuration.RabbitMqConfiguration.NOTIFICATION_QUEUE;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(RabbitAutoConfiguration.class)
public class NotificationService {

    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @SneakyThrows
    @EventListener(EventNotification.class)
    public void sendNotification(EventNotification event) {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsString(new Notification(
                        Integer.parseInt(event.getId()),
                        event.getTitle()
                )).getBytes())
                .setContentType(CONTENT_TYPE_JSON)
                .build();
        rabbitTemplate.send(NOTIFICATION_QUEUE, message);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Notification {
        private long id;
        private String title;
    }
}
