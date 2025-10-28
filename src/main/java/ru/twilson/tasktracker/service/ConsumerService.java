package ru.twilson.tasktracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.repository.ConsumerRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {//todo Избавиться

    private final ConsumerRepository consumerRepository;

    public Consumer add(Consumer consumer) {
        if (consumer == null) {
            throw new NullPointerException("Consumer is null");
        }
        log.info("Adding consumer {}", consumer);
        return consumerRepository.saveAndFlush(consumer);
    }
}
