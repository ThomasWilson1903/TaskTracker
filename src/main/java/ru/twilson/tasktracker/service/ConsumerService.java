package ru.twilson.tasktracker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.repository.ConsumerRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ConsumerRepository consumerRepository;

    public Consumer add(Consumer consumer) {
        Consumer copy = consumer.copy();
        copy.setGlobalId(UUID.randomUUID().toString());
        return consumerRepository.saveAndFlush(copy);
    }

    public boolean isExists(String username, String password) {
        return consumerRepository
                .findByUsername(username)
                .map(consumer -> consumer.getPassword().equals(password))
                .orElse(false);
    }
}
