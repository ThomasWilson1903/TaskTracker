package ru.twilson.tasktracker.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.repository.ConsumerRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final ConsumerRepository consumerRepository;

    public Consumer add(Consumer consumer) {
        Consumer copy = consumer.copy();
        copy.setGlobalId(UUID.randomUUID().toString());
        try {
            return consumerRepository.saveAndFlush(copy);
        } catch (DataIntegrityViolationException exception) {
            log.info(exception.getMessage(), exception);
            throw new RuntimeException("The user already exists");
        }
    }

    public boolean isExists(String username) {
        Optional<Consumer> byUsername = consumerRepository
                .findByUsername(username);
        return byUsername.isPresent();
    }

    public Consumer findConsumer(String username, String password) {
        Consumer consumer = consumerRepository
                .findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new EntityNotFoundException("Consumer not found"));
        return consumer.copyPasswordEmpty();
    }

    private Map<String, Object> objects = new ConcurrentHashMap<>();

    public synchronized Object getObjects() {
        if (objects.containsKey("123")) {
            synchronized (this) {
                if (objects.containsKey("123")) {

                }
            }
        }
        return this.objects;
    }
}
