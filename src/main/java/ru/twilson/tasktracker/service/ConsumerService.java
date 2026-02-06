package ru.twilson.tasktracker.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
        copy.setEnable(true);
        try {
            return consumerRepository.saveAndFlush(copy);
        } catch (DataIntegrityViolationException exception) {
            log.info(exception.getMessage(), exception);
            throw new RuntimeException("The user already exists");
        }
    }

    public boolean isExists(String username) {
        return consumerRepository.existsByUsername(username);
    }

    public void isEnable(String username) throws AccessDeniedException, EntityNotFoundException {
        Consumer consumer = consumerRepository
                .findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Consumer not found"));
        if (!consumer.isEnable()) {
            throw new AccessDeniedException("the user is blocked");
        }
    }

    public Consumer findConsumer(String username, String password) {
        Consumer consumer = consumerRepository
                .findByUsernameAndPassword(username, password)
                .orElseThrow(() -> new EntityNotFoundException("Consumer not found"));
        return consumer.copyPasswordEmpty();
    }

    public Consumer findConsumer(String username) {
        Consumer consumer = consumerRepository
                .findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Consumer not found"));
        return consumer.copyPasswordEmpty();
    }
}
