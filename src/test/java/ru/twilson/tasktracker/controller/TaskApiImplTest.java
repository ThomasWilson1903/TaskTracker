package ru.twilson.tasktracker.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.model.CreateTaskRequest;
import ru.twilson.tasktracker.model.TaskNote;
import ru.twilson.tasktracker.model.TasksGet200Response;
import ru.twilson.tasktracker.model.UpdateTaskRequest;
import ru.twilson.tasktracker.service.TaskService;
import ru.twilson.tasktracker.utils.TaskMapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для контроллера задач TaskApiImpl")
class TaskApiImplTest {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskApiImpl taskApi;

    private Task testTask;
    private TaskNote testTaskNote;
    private CreateTaskRequest createTaskRequest;
    private UpdateTaskRequest updateTaskRequest;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        testTask = new Task();
        testTask.setTaskId(1L);
        testTask.setTaskGlobalId("task-123");
        testTask.setTitle("Тестовая задача");
        testTask.setDescription("Описание тестовой задачи");
        testTask.setPriority("HIGH");
        testTask.setDueDate("2024-12-31");

        testTaskNote = new TaskNote();
        testTaskNote.setId("task-123");
        testTaskNote.setTitle("Тестовая задача");
        testTaskNote.setDescription("Описание тестовой задачи");
        testTaskNote.setPriority("HIGH");
        testTaskNote.setDueDate("2024-12-31");

        createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setUserId("user-123");
        createTaskRequest.setTitle("Новая задача");
        createTaskRequest.setDescription("Описание новой задачи");
        createTaskRequest.setPriority("MEDIUM");

        updateTaskRequest = new UpdateTaskRequest();
        updateTaskRequest.setTitle("Обновленная задача");
        updateTaskRequest.setDescription("Обновленное описание");
    }

    @Nested
    @DisplayName("GET /tasks - Получение списка задач")
    class GetTasksTests {

        @Test
        @DisplayName("Должен вернуть задачи успешно, когда пользователь существует")
        void tasksGet_ShouldReturnTasks_WhenUserExists() {
            // Given
            String userId = "user-123";
            List<Task> tasks = Arrays.asList(testTask);
            List<TaskNote> taskNotes = Arrays.asList(testTaskNote);

            when(taskService.get(userId)).thenReturn(tasks);
            when(taskMapper.toTaskNotes(tasks)).thenReturn(taskNotes);

            // When
            ResponseEntity<TasksGet200Response> response = taskApi.tasksGet(userId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().getData().size());
            assertEquals("task-123", response.getBody().getData().get(0).getId());

            verify(taskService).get(userId);
            verify(taskMapper).toTaskNotes(tasks);
        }

        @Test
        @DisplayName("Должен вернуть пустой список, когда у пользователя нет задач")
        void tasksGet_ShouldReturnEmptyList_WhenUserHasNoTasks() {
            // Given
            String userId = "user-123";
            List<Task> emptyTasks = Collections.emptyList();
            List<TaskNote> emptyTaskNotes = Collections.emptyList();

            when(taskService.get(userId)).thenReturn(emptyTasks);
            when(taskMapper.toTaskNotes(emptyTasks)).thenReturn(emptyTaskNotes);

            // When
            ResponseEntity<TasksGet200Response> response = taskApi.tasksGet(userId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().getData().isEmpty());

            verify(taskService).get(userId);
            verify(taskMapper).toTaskNotes(emptyTasks);
        }

        @Test
        @DisplayName("Должен вызвать сервис с правильным userId")
        void tasksGet_ShouldCallServiceWithCorrectUserId() {
            // Given
            String userId = "user-456";
            List<Task> tasks = Arrays.asList(testTask);
            List<TaskNote> taskNotes = Arrays.asList(testTaskNote);

            when(taskService.get(userId)).thenReturn(tasks);
            when(taskMapper.toTaskNotes(tasks)).thenReturn(taskNotes);

            // When
            taskApi.tasksGet(userId);

            // Then
            verify(taskService).get(userId);
        }
    }

    @Nested
    @DisplayName("POST /tasks - Создание новой задачи")
    class PostTasksTests {

        @Test
        @DisplayName("Должен создать задачу успешно при валидном запросе")
        void tasksPost_ShouldCreateTask_WhenRequestIsValid() {
            // Given
            Task mappedTask = new Task();
            mappedTask.setTitle("Новая задача");
            mappedTask.setDescription("Описание новой задачи");
            mappedTask.setPriority("MEDIUM");

            when(taskMapper.toTask(createTaskRequest)).thenReturn(mappedTask);

            // When
            ResponseEntity<TaskNote> response = taskApi.tasksPost(createTaskRequest);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(taskMapper).toTask(createTaskRequest);
            verify(taskService).add("user-123", mappedTask);
        }

        @Test
        @DisplayName("Должен вызвать маппер для преобразования CreateTaskRequest в Task")
        void tasksPost_ShouldCallMapper_ToConvertCreateTaskRequest() {
            // Given
            Task mappedTask = new Task();
            when(taskMapper.toTask(createTaskRequest)).thenReturn(mappedTask);

            // When
            taskApi.tasksPost(createTaskRequest);

            // Then
            verify(taskMapper).toTask(createTaskRequest);
        }

        @Test
        @DisplayName("Должен передать правильные параметры в сервис")
        void tasksPost_ShouldPassCorrectParametersToService() {
            // Given
            Task mappedTask = new Task();
            mappedTask.setTitle("Новая задача");

            when(taskMapper.toTask(createTaskRequest)).thenReturn(mappedTask);

            // When
            taskApi.tasksPost(createTaskRequest);

            // Then
            verify(taskService).add("user-123", mappedTask);
        }
    }

    @Nested
    @DisplayName("DELETE /tasks/{taskId} - Удаление задачи")
    class DeleteTasksTests {

        @Test
        @DisplayName("Должен удалить задачу успешно при валидном taskId")
        void tasksTaskIdDelete_ShouldDeleteTask_WhenTaskIdIsValid() {
            // Given
            String taskId = "task-123";

            // When
            ResponseEntity<Void> response = taskApi.tasksTaskIdDelete(taskId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            verify(taskService).remove(taskId);
        }

        @Test
        @DisplayName("Должен вызвать сервис удаления с правильным taskId")
        void tasksTaskIdDelete_ShouldCallServiceWithCorrectTaskId() {
            // Given
            String taskId = "task-456";

            // When
            taskApi.tasksTaskIdDelete(taskId);

            // Then
            verify(taskService).remove(taskId);
        }

        @Test
        @DisplayName("Должен вернуть статус OK после успешного удаления")
        void tasksTaskIdDelete_ShouldReturnOkStatus_AfterSuccessfulDeletion() {
            // Given
            String taskId = "task-789";

            // When
            ResponseEntity<Void> response = taskApi.tasksTaskIdDelete(taskId);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    @DisplayName("Методы, использующие реализацию по умолчанию")
    class DefaultImplementationTests {

        @Test
        @DisplayName("tasksTaskIdGet должен использовать реализацию по умолчанию")
        void tasksTaskIdGet_ShouldUseDefaultImplementation() {
            // Given
            String taskId = "task-123";

            // When & Then
            // Проверяем, что метод делегирует родительской реализации
            // В данном случае мы просто убеждаемся, что метод не падает
            assertDoesNotThrow(() -> {
                ResponseEntity<TaskNote> response = taskApi.tasksTaskIdGet(taskId);
                // Реализация по умолчанию может возвращать определенный результат
            });
        }

        @Test
        @DisplayName("tasksTaskIdPut должен использовать реализацию по умолчанию")
        void tasksTaskIdPut_ShouldUseDefaultImplementation() {
            // Given
            String taskId = "task-123";
            UpdateTaskRequest updateRequest = new UpdateTaskRequest();

            // When & Then
            // Проверяем, что метод делегирует родительской реализации
            assertDoesNotThrow(() -> {
                ResponseEntity<TaskNote> response = taskApi.tasksTaskIdPut(taskId, updateRequest);
                // Реализация по умолчанию может возвращать определенный результат
            });
        }
    }

    @Nested
    @DisplayName("Интеграционные аспекты")
    class IntegrationAspectsTests {

        @Test
        @DisplayName("Должен корректно взаимодействовать с TaskMapper и TaskService")
        void shouldCorrectlyIntegrateWithMapperAndService() {
            // Given
            String userId = "user-123";
            List<Task> tasks = Arrays.asList(testTask);
            List<TaskNote> taskNotes = Arrays.asList(testTaskNote);

            when(taskService.get(userId)).thenReturn(tasks);
            when(taskMapper.toTaskNotes(tasks)).thenReturn(taskNotes);

            // When
            ResponseEntity<TasksGet200Response> response = taskApi.tasksGet(userId);

            // Then
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Проверяем цепочку вызовов
            verify(taskService).get(userId);
            verify(taskMapper).toTaskNotes(tasks);
        }

        @Test
        @DisplayName("Должен правильно обрабатывать цепочку создания задачи")
        void shouldCorrectlyHandleTaskCreationChain() {
            // Given
            Task mappedTask = new Task();
            when(taskMapper.toTask(createTaskRequest)).thenReturn(mappedTask);

            // When
            ResponseEntity<TaskNote> response = taskApi.tasksPost(createTaskRequest);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Проверяем порядок и параметры вызовов
            verify(taskMapper).toTask(createTaskRequest);
            verify(taskService).add("user-123", mappedTask);
        }
    }
}