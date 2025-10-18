package ru.twilson.tasktracker.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.repository.ConsumerRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @InjectMocks
    private ConsumerService consumerService;

    @Mock
    ConsumerRepository consumerRepository;

    @Nested
    @DisplayName("Тестирование добавление пользователя")
    class TestAdd {

        @Test
        @DisplayName("Добавление пользователя")
        void add() {
            Consumer consumer = Consumer.builder()
                    .globalId(UUID.randomUUID().toString())
                    .id(1234L)
                    .tasks(List.of(new Task()))
                    .username("username")
                    .firstName("firstName")
                    .lastName("lastName")
                    .password("password")
                    .build();
            when(consumerRepository.saveAndFlush(any())).thenReturn(consumer);
            Consumer result = consumerService.add(consumer);
            assertNotNull(result);
            assertEquals(consumer, result);
        }

        @Test
        @DisplayName("При добавлении пользователя произошла ошибка")
        void addThrowsException() {
            when(consumerRepository.saveAndFlush(any(Consumer.class))).thenThrow(new RuntimeException());
            assertThrows(RuntimeException.class, () -> consumerService.add(new Consumer()));
        }

        @Test
        @DisplayName("Попытка добавление null")
        void addNullThrowsException() {
            assertThrows(NullPointerException.class, () -> consumerService.add(null));
        }
    }
}