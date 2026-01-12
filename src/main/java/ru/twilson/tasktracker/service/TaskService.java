package ru.twilson.tasktracker.service;

import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import ru.twilson.tasktracker.configuration.EventNotification;

import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.repository.ConsumerRepository;
import ru.twilson.tasktracker.repository.TaskRepository;
import ru.twilson.tasktracker.utils.TaskFormatterUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.twilson.tasktracker.utils.TaskFormatterUtils.formatTask;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ConsumerRepository consumerRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Task getTaskByIdTask(String taskId) {
        return taskRepository.findByTaskGlobalId(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @Transactional
    public List<Task> getTaskByIdConsumer(String consumerGlobalId) {
        if (consumerGlobalId == null) {
            throw new NullPointerException("consumerGlobalId is null");
        }
        Consumer consumer = getConsumer(consumerGlobalId);
        checkAccess(consumer.getUsername());
        return consumer.getTasks().stream().map(Task::copy).toList();
    }

    @Transactional
    public Task add(String consumerGlobalId, Task taskTemplate) {
        if (consumerGlobalId == null || taskTemplate == null) {
            throw new NullPointerException("null");
        }
        Consumer consumer = getConsumer(consumerGlobalId);
        checkAccess(consumer.getUsername());
        Task task = taskTemplate.copy();
        task.setTaskGlobalId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now().toString());
        task.setUpdatedAt(Instant.now().toString());
        task.setStatus("pending");
        task.setConsumer(consumer);
        consumer.addTask(task);
        consumerRepository.save(consumer);
        applicationEventPublisher.publishEvent(new EventNotification(this, consumerGlobalId, formatTask(task)));
        return task;
    }

    @Transactional
    public Task update(Task task) {
        Task taskEntity = taskRepository.findByTaskGlobalId(task.getTaskGlobalId()).orElseThrow(() ->
                new EntityNotFoundException("Task not found"));
        Consumer consumer = taskEntity.getConsumer();
        checkAccess(consumer.getUsername());
        String notification = TaskFormatterUtils.update(taskEntity, task);
        taskEntity.setUpdatedAt(Instant.now().toString());
        taskEntity.setTitle(task.getTitle() == null ? taskEntity.getTitle() : task.getTitle());
        taskEntity.setDescription(task.getDescription() == null ? taskEntity.getDescription() : task.getDescription());
        taskEntity.setPriority(task.getPriority() == null ? taskEntity.getPriority() : task.getPriority());
        taskEntity.setDueDate(task.getDueDate() == null ? taskEntity.getDueDate() : task.getDueDate());
        taskEntity.setStatus(task.getStatus() == null ? taskEntity.getStatus() : task.getStatus());
        taskEntity.setCreatedAt(task.getCreatedAt() == null ? taskEntity.getCreatedAt() : task.getCreatedAt());
        taskEntity.setConsumer(task.getConsumer() == null ? taskEntity.getConsumer() : task.getConsumer());
        taskEntity.setCompletedAt("completed".equals(task.getStatus()) ? Instant.now().toString() : null);
        applicationEventPublisher.publishEvent(new EventNotification(this, taskEntity.getConsumer().getGlobalId(), notification));
        return taskEntity.copy();
    }

    @Transactional
    public void remove(String taskGlobalId) {
        if (taskGlobalId == null) return;
        Optional<Task> byTaskGlobalId = taskRepository.findByTaskGlobalId(taskGlobalId);
        if (byTaskGlobalId.isEmpty()) return;
        Task task = byTaskGlobalId.get();
        checkAccess(task.getConsumer().getUsername());
        task.setDeleted(true);
        taskRepository.saveAndFlush(task);
        applicationEventPublisher.publishEvent(new EventNotification(this, task.getConsumer().getGlobalId(), String.format("Задача была удалена [%s]", task.getTitle())));
    }

    private Consumer getConsumer(String consumerGlobalId) {
        return consumerRepository.findByGlobalId(
                consumerGlobalId).orElseThrow(() -> new EntityNotFoundException("Consumer not found"));
    }

    private void checkAccess(String username) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (!authentication.getName().equals(username)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
