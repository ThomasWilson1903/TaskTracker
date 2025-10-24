package ru.twilson.tasktracker.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnBean(RabbitAutoConfiguration.class)
public class RabbitMqConfiguration {

    public static final String NOTIFICATION_QUEUE = "task.tracker.bot.notification";

    @Bean
    public Queue vpsQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

}
