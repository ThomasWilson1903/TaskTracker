package ru.twilson.tasktracker.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConsumerTest {

    @Nested
    @DisplayName("Добавление новой задачи Consumer")
    class AddTaskInConsumerTest {

        @Test
        @DisplayName("Добавление задачи")
        void addTask() {
            //Given
            Task task = Task.builder().build();
            Consumer consumer = Consumer.builder()
                    .tasks(new ArrayList<>(List.of(
                            Task.builder()
                                    .taskGlobalId(UUID.randomUUID().toString())
                                    .build(),
                            Task.builder()
                                    .taskGlobalId(UUID.randomUUID().toString())
                                    .build()
                    )))
                    .build();
            //When
            Consumer result = consumer.addTask(task);
            //Then
            assertEquals(3, result.getTasks().size());
            assertEquals(task, consumer.getTasks().get(consumer.getTasks().size() - 1));
        }

        @Test
        @DisplayName("Добавление задачи в пустой лист")
        void addTaskEmptyList() {
            //Given
            Task task = Task.builder().build();
            Consumer consumer = Consumer.builder()
                    .tasks(new ArrayList<>())
                    .build();
            //When
            Consumer result = consumer.addTask(task);
            //Then
            assertEquals(1, result.getTasks().size());
            assertEquals(task, consumer.getTasks().get(consumer.getTasks().size() - 1));
        }

        @Test
        @DisplayName("Добавление задачи в лист null")
        void addTaskListNull() {
            //Given
            Task task = Task.builder().build();
            Consumer consumer = Consumer.builder()
                    .tasks(null)
                    .build();
            //When
            Consumer result = consumer.addTask(task);
            //Then
            assertNotNull(result);
            assertNotNull(result.getTasks());
            assertEquals(task, result.getTasks().get(result.getTasks().size() - 1));
        }

        @Test
        @DisplayName("Добавление задачи null")
        void addTaskNull() {
            //Given
            Consumer consumer = Consumer.builder().build();
            //When
            Consumer result = consumer.addTask(null);
            //Then
            assertNotNull(result);
        }

    }

    @Nested
    @DisplayName("Удаление задачи из Consumer")
    class RemoveTaskInConsumerTest {

        @Test
        @DisplayName("Удаление задачи")
        void removeTask() {
            //Given
            Consumer consumer = Consumer.builder()
                    .tasks(new ArrayList<>(List.of(
                            Task.builder()
                                    .taskGlobalId(UUID.randomUUID().toString())
                                    .build(),
                            Task.builder()
                                    .taskGlobalId(UUID.randomUUID().toString())
                                    .build()
                    )))
                    .build();
            //When
            consumer.removeTask(consumer.getTasks().get(0).getTaskGlobalId());
            //Then
            assertEquals(1, consumer.getTasks().size());
        }

        @Test
        @DisplayName("Попытка удалить по не существующему globalId")
        void removeTaskOrderGlobalId() {
            //Given
            Consumer consumer = Consumer.builder()
                    .tasks(new ArrayList<>(List.of(
                            Task.builder()
                                    .taskGlobalId(UUID.randomUUID().toString())
                                    .build(),
                            Task.builder()
                                    .taskGlobalId(UUID.randomUUID().toString())
                                    .build()
                    )))
                    .build();
            //When
            consumer.removeTask("order");
            //Then
            assertEquals(2, consumer.getTasks().size());
        }

        @Test
        @DisplayName("Попытка удалить по globalId = null")
        void removeTaskNullGlobalId() {
            //Given
            Consumer consumer = Consumer.builder()
                    .tasks(new ArrayList<>(List.of(
                            Task.builder()
                                    .taskGlobalId(UUID.randomUUID().toString())
                                    .build(),
                            Task.builder()
                                    .taskGlobalId(UUID.randomUUID().toString())
                                    .build()
                    )))
                    .build();
            //When
            consumer.removeTask(null);
            //Then
            assertEquals(2, consumer.getTasks().size());
        }

        @Test
        @DisplayName("Попытка удалить в null списке")
        void removeTaskNullList() {
            //Given
            Consumer consumer = Consumer.builder()
                    .tasks(null)
                    .build();
            //When
            consumer.removeTask(UUID.randomUUID().toString());
            //Then
            assertEquals(0, consumer.getTasks().size());
            assertNotNull(consumer.getTasks());
        }

        @Test
        @DisplayName("Попытка удалить в пустом  списке")
        void removeTaskEmptyList() {
            //Given
            Consumer consumer = Consumer.builder()
                    .tasks(null)
                    .build();
            //When
            consumer.removeTask(UUID.randomUUID().toString());
            //Then
            assertNotNull(consumer.getTasks());
            assertEquals(0, consumer.getTasks().size());
            assertTrue(consumer.getTasks().isEmpty());
        }
    }

}