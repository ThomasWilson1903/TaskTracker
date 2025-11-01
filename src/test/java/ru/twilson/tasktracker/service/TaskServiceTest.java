package ru.twilson.tasktracker.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.repository.ConsumerRepository;
import ru.twilson.tasktracker.repository.TaskRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ConsumerRepository consumerRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Nested
    @DisplayName("Тестирование получение списка задач")
    class GetTasksTest {

        @Test
        @DisplayName("Получение списка задач")
        void getTasks() {
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
            assertEquals(consumer.getTasks().size(), tasks.size());
            assertEquals(consumer.getTasks().get(0), task);
        }

        @Test
        @DisplayName("Получение задач у не существующего пользователя")
        void getTasksForNonExistentConsumer() {
            //Given
            String globalId = UUID.randomUUID().toString();
            when(consumerRepository.findByGlobalId(globalId))
                    .thenReturn(Optional.empty());
            //When and Then
            assertThrows(EntityNotFoundException.class, () -> taskService.getTaskByIdConsumer(globalId));
        }

        @Test
        @DisplayName("Попытка получения задачи по NULL globalID")
        void getTaskNullGlobalId() {
            // When and Then
            assertThrows(NullPointerException.class, () -> taskService.getTaskByIdConsumer(null));
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
            when(consumerRepository.findByGlobalId(globalId)).thenReturn(Optional.of(consumer));
            //When
            taskService.add(globalId, task);
            //Then
            verify(consumerRepository).save(any(Consumer.class));
        }

        @Test
        @DisplayName("Получения не существующего пользователя, регистрация нового")
        void addTaskAndNewConsumer() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Task task = Task.builder()
                    .taskGlobalId(UUID.randomUUID().toString())
                    .title("title")
                    .description("description")
                    .build();
            when(consumerRepository.findByGlobalId(globalId)).thenReturn(Optional.empty());
            //When
            Task result = taskService.add(globalId, task);
            assertEquals(task.getTitle(), result.getTitle());
            assertEquals(task.getDescription(), result.getDescription());
        }

        @Test
        @DisplayName("Попытка добавить задачу, consumerGlobalId = null")
        void addTaskConsumerGlobalIdNull() {
            //When and Then
            assertThrows(NullPointerException.class, () ->
                    taskService.add(null, new Task()));
        }

        @Test
        @DisplayName("Попытка добавить null задачу")
        void addTaskTaskNull() {
            //When and Then
            assertThrows(NullPointerException.class, () ->
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
        @DisplayName("Попытка удалить несуществующую таску, не должно падать исключение")
        void deleteTaskNotFoundTask_NotThrowsException() {
            //Given
            when(taskRepository.findByTaskGlobalId(any(String.class))).thenReturn(Optional.empty());
            //When
            Assertions.assertDoesNotThrow(() -> taskService.remove(UUID.randomUUID().toString()));
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

    @Nested
    @DisplayName("Тестирование получение задачи по id")
    class GetTaskTest {

        @Test
        @DisplayName("Получение задачи по id")
        public void getTask() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Task task = Task.builder()
                    .taskGlobalId(globalId)
                    .build();
            when(taskRepository.findByTaskGlobalId(any(String.class))).thenReturn(Optional.of(task));
            //When
            Task taskResult = taskService.getTaskByIdTask(globalId);
            //Then
            assertEquals(task, taskResult);
        }

        @Test
        @DisplayName("При получение задачи по id, задача не была найдена, падает исключение ")
        public void getTaskNullTask_ThrowsException() {
            //Given
            String globalId = UUID.randomUUID().toString();
            when(taskRepository.findByTaskGlobalId(any(String.class))).thenReturn(Optional.empty());
            //When and Then
            assertThrows(EntityNotFoundException.class, () -> taskService.getTaskByIdTask(globalId));
        }
    }

    @Nested
    @DisplayName("Тестирование изменение задачи")
    class UpdateTaskTest {

        @Test
        @DisplayName("Изменение задачи")
        public void updateTask() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Task taskUpdate = Task.builder()
                    .taskGlobalId(globalId)
                    .build();
            when(taskRepository.findByTaskGlobalId(any(String.class)))
                    .thenReturn(Optional.of(Task
                            .builder()
                            .taskGlobalId(globalId)
                            .consumer(Consumer.builder().globalId(UUID.randomUUID().toString()).build())
                            .build()));
            //When
            Task taskResult = taskService.update(taskUpdate);
            //Then
            assertEquals(taskUpdate.getTaskGlobalId(), taskResult.getTaskGlobalId());
            assertEquals(taskUpdate.getTitle(), taskResult.getTitle());
            assertEquals(taskUpdate.getDescription(), taskResult.getDescription());
            assertEquals(taskUpdate.getPriority(), taskResult.getPriority());
            assertEquals(taskUpdate.getDueDate(), taskResult.getDueDate());
            assertEquals(taskUpdate.getCreatedAt(), taskResult.getCreatedAt());
            assertEquals(taskUpdate.getStatus(), taskResult.getStatus());
        }

        @Test
        @DisplayName("Изменение всех полей задачи")
        public void updateTaskAllFile() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Task taskUpdate = Task.builder()
                    .taskGlobalId(globalId)
                    .title("title")
                    .description("description")
                    .priority("low")
                    .dueDate(Instant.now().toString())
                    .createdAt(Instant.now().toString())
                    .status("pending")
                    .build();
            when(taskRepository.findByTaskGlobalId(any(String.class)))
                    .thenReturn(Optional.of(
                            Task.builder()
                                    .taskGlobalId(globalId)
                                    .consumer(Consumer.builder().globalId(UUID.randomUUID().toString()).build())
                                    .build()));
            //When
            Task taskResult = taskService.update(taskUpdate);
            //Then
            assertNull(taskResult.getCompletedAt());
            assertEquals(taskUpdate.getTaskGlobalId(), taskResult.getTaskGlobalId());
            assertEquals(taskUpdate.getTitle(), taskResult.getTitle());
            assertEquals(taskUpdate.getDescription(), taskResult.getDescription());
            assertEquals(taskUpdate.getPriority(), taskResult.getPriority());
            assertEquals(taskUpdate.getDueDate(), taskResult.getDueDate());
            assertEquals(taskUpdate.getCreatedAt(), taskResult.getCreatedAt());
            assertEquals(taskUpdate.getStatus(), taskResult.getStatus());
        }

        @Test
        @DisplayName("Изменение статуса на 'completed'")
        public void updateTaskSetCompleted() {
            //Given
            String globalId = UUID.randomUUID().toString();
            Task taskUpdate = Task.builder()
                    .taskGlobalId(globalId)
                    .title("1234")
                    .description("1234")
                    .status("completed")
                    .build();
            when(taskRepository.findByTaskGlobalId(any(String.class)))
                    .thenReturn(Optional.of(Task
                            .builder()
                            .taskGlobalId(globalId)
                            .consumer(Consumer.builder().globalId(UUID.randomUUID().toString()).build())
                            .build()));
            //When
            Task taskResult = taskService.update(taskUpdate);
            //Then
            assertNotNull(taskResult.getCompletedAt());
            assertEquals(taskUpdate.getTaskGlobalId(), taskResult.getTaskGlobalId());
            assertEquals(taskUpdate.getTitle(), taskResult.getTitle());
            assertEquals(taskUpdate.getDescription(), taskResult.getDescription());
            assertEquals(taskUpdate.getPriority(), taskResult.getPriority());
            assertEquals(taskUpdate.getDueDate(), taskResult.getDueDate());
            assertEquals(taskUpdate.getCreatedAt(), taskResult.getCreatedAt());
            assertEquals(taskUpdate.getStatus(), taskResult.getStatus());
        }

        @Test
        @DisplayName("Задача не была найдена, падает исключение")
        public void updateTaskErrorNullTask_ThrowsException() {
            //Given
            when(taskRepository.findByTaskGlobalId(any(String.class))).thenReturn(Optional.empty());
            //When and Then
            assertThrows(EntityNotFoundException.class, () ->
                    taskService.update(Task.builder().taskGlobalId(UUID.randomUUID().toString()).build()));
        }
    }
}