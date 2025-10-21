package ru.twilson.tasktracker.service;

import org.junit.jupiter.api.Assertions;
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
import ru.twilson.tasktracker.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ConsumerService consumerService;
    @Mock
    private ConsumerRepository consumerRepository;

    @Nested
    @DisplayName("Тестирование получение списка задач")
    class GetTaskTest {

        @Test
        @DisplayName("Получение задач у не существующего пользователя")
        void getConsumerOptionalEmpty() {
            //Given
            String globalId = UUID.randomUUID().toString();
            when(consumerRepository.findByGlobalId(globalId))
                    .thenReturn(Optional.empty());
            //When
            List<Task> tasks = taskService.getTaskByIdConsumer(globalId);
            //Then
            Assertions.assertNotNull(tasks);
            Assertions.assertTrue(tasks.isEmpty());
        }

        @Test
        @DisplayName("Получение списка задач")
        void getTask() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Task task = Task.builder()
                    .taskGlobalId(UUID.randomUUID().toString())
                    .build();
            Consumer consumer = Consumer.builder()
                    .globalId(globalId)
                    .tasks(List.of(task))
                    .build();
            when(consumerRepository.findByGlobalId(globalId)).thenReturn(Optional.of(consumer));
            //When
            List<Task> tasks = taskService.getTaskByIdConsumer(globalId);
            //Then
            Assertions.assertEquals(consumer.getTasks().size(), tasks.size());
            Assertions.assertEquals(consumer.getTasks().get(0), task);
        }

        @Test
        @DisplayName("Ошибка от БД при получении задачи")
        void getTaskThrowsException() {
            //Given
            when(consumerRepository.findByGlobalId(any())).thenThrow(new RuntimeException());
            //When and Then
            Assertions.assertThrows(RuntimeException.class, () -> taskService.getTaskByIdConsumer(UUID.randomUUID().toString()));
        }

        @Test
        @DisplayName("Попытка получения задачи по NULL globalID")
        void getTaskNullGlobalId() {
            // When and Then
            Assertions.assertThrows(NullPointerException.class, () -> taskService.getTaskByIdConsumer(null));
        }
    }

    @Nested
    @DisplayName("Тестирование добавление задачи")
    class AddTaskTest {

        @Test
        @DisplayName("Добавление задачи")
        void addTask() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Consumer consumer = Consumer.builder()
                    .globalId(globalId)
                    .tasks(new ArrayList<>())
                    .build();
            Task task = Task.builder()
                    .taskGlobalId(UUID.randomUUID().toString())
                    .build();
            when(consumerService.add(any(Consumer.class))).thenReturn(consumer);
            when(consumerRepository.findByGlobalId(globalId)).thenReturn(Optional.of(consumer));
            //When
            taskService.add(globalId, task);
            //Then
            verify(consumerService).add(any(Consumer.class));
        }

        @Test
        @DisplayName("Ошибка при сохранении пользователя с новой задачей")
        void addTaskErrorSave() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Consumer consumer = Consumer.builder()
                    .globalId(globalId)
                    .tasks(new ArrayList<>())
                    .build();
            Task task = Task.builder()
                    .taskGlobalId(UUID.randomUUID().toString())
                    .build();
            when(consumerService.add(any(Consumer.class))).thenThrow(new RuntimeException());
            when(consumerRepository.findByGlobalId(globalId)).thenReturn(Optional.of(consumer));
            //When and Then
            Assertions.assertThrows(RuntimeException.class, () -> taskService.add(globalId, task));
        }

        @Test
        @DisplayName("Ошибка от БД при получении пользователя")
        void addTaskThrowsException() {
            //Given
            when(consumerRepository.findByGlobalId(any())).thenThrow(new RuntimeException());
            //When and Then
            Assertions.assertThrows(RuntimeException.class, () -> taskService.add(UUID.randomUUID().toString(), new Task()));
        }

        @Test
        @DisplayName("Получение не существующего пользователя, регистрация нового")
        void addTaskAndNewConsumer() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Consumer consumer = Consumer.builder()
                    .globalId(globalId)
                    .tasks(new ArrayList<>())
                    .build();
            Task task = Task.builder()
                    .taskGlobalId(UUID.randomUUID().toString())
                    .build();
            when(consumerRepository.findByGlobalId(globalId)).thenReturn(Optional.empty());
            when(consumerService.add(any(Consumer.class))).thenReturn(consumer);
            //When
            taskService.add(globalId, task);

            //Then
            verify(consumerService, times(1)).add(any(Consumer.class));
        }

        @Test
        @DisplayName("Попытка добавить задачу, consumerGlobalId = null")
        void addTaskConsumerGlobalIdNull() {
            //When and Then
            Assertions.assertThrows(NullPointerException.class, () ->
                    taskService.add(null, new Task()));
        }

        @Test
        @DisplayName("Попытка добавить null задачу")
        void addTaskTaskNull() {
            //When and Then
            Assertions.assertThrows(NullPointerException.class, () ->
                    taskService.add(UUID.randomUUID().toString(), null));
        }
    }

    @Nested
    @DisplayName("Тестирование удаление задачи")
    class DeleteTaskTest {

        @Test
        @DisplayName("Удаление задачи")
        void deleteTask() {
            //Given
            var taskGlobalId = UUID.randomUUID().toString();
            Task task = Task.builder()
                    .taskGlobalId(taskGlobalId)
                    .consumer(Consumer.builder()
                            .globalId(UUID.randomUUID().toString())
                            .build())
                    .build();
            when(taskRepository.findByTaskGlobalId(taskGlobalId)).thenReturn(Optional.of(task));
            //When
            taskService.remove(taskGlobalId);
            //Then
            verify(consumerRepository, times(1)).saveAndFlush(any(Consumer.class));
        }

        @Test
        @DisplayName("Попытка удалить несуществующую таску")
        void deleteTaskNotFoundTask() {
            //Given
            when(taskRepository.findByTaskGlobalId(any(String.class))).thenReturn(Optional.empty());
            //When
            taskService.remove(UUID.randomUUID().toString());
            //Then
            verify(consumerRepository, never()).saveAndFlush(any(Consumer.class));

        }

        @Test
        @DisplayName("Попытка удалить по taskGlobalId = null")
        void deleteTaskTaskGlobalIdNull() {
            //When
            taskService.remove(null);
            //Then
            verify(consumerRepository, never()).saveAndFlush(any(Consumer.class));
        }
    }
}