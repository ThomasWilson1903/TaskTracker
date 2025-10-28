package ru.twilson.tasktracker.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.stereotype.Service;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.repository.ConsumerRepository;
import ru.twilson.tasktracker.repository.TaskRepository;
import ru.twilson.tasktracker.utils.TaskFormatterUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ConsumerRepository consumerRepository;
//    private final NotificationService notificationService;

    public Task getTaskByIdTask(String taskId) {
        return taskRepository.findByTaskGlobalId(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    @Transactional
    public List<Task> getTaskByIdConsumer(String consumerGlobalId) {
        if (consumerGlobalId == null) {
            throw new NullPointerException("consumerGlobalId is null");
        }
        Consumer consumer = consumerRepository.findByGlobalId(
                consumerGlobalId).orElseThrow(() -> new EntityNotFoundException("Consumer not found"));
        return consumer.getTasks().stream().map(Task::copy).toList();
    }

    @Transactional
    public Task add(String consumerGlobalId, Task taskTemplate) {
        if (consumerGlobalId == null || taskTemplate == null) {
            throw new NullPointerException("null");
        }
        Task task = taskTemplate.copy();
        Optional<Consumer> consumerOptional = consumerRepository.findByGlobalId(consumerGlobalId);
        task.setTaskGlobalId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now().toString());
        task.setUpdatedAt(Instant.now().toString());
        task.setStatus("pending");
        Consumer consumer;
        if (consumerOptional.isEmpty()) {
            consumer = Consumer.builder().globalId(consumerGlobalId).build();
            task.setConsumer(consumer);
            consumer.addTask(task);
        } else {
            consumer = consumerOptional.get();
            task.setConsumer(consumer);
            consumer.addTask(task);
        }
        consumerRepository.save(consumer);
//        notificationService.sendNotification(consumerGlobalId, formatTask(task));
        return task;
    }

    @Transactional
    public Task update(Task task) {
        Task taskEntity = taskRepository.findByTaskGlobalId(task.getTaskGlobalId()).orElseThrow(() ->
                new EntityNotFoundException("Task not found"));
        String notification = TaskFormatterUtils.update(taskEntity, task);
        taskEntity.setUpdatedAt(Instant.now().toString());
        taskEntity.setTitle(task.getTitle() == null ? taskEntity.getTitle() : task.getTitle());
        taskEntity.setDescription(task.getDescription() == null ? taskEntity.getDescription() : task.getDescription());
        taskEntity.setPriority(task.getPriority() == null ? taskEntity.getPriority() : task.getPriority());
        taskEntity.setDueDate(task.getDueDate() == null ? taskEntity.getDueDate() : task.getDueDate());
        taskEntity.setStatus(task.getStatus() == null ? taskEntity.getStatus() : task.getStatus());
        taskEntity.setCreatedAt(task.getCreatedAt() == null ? taskEntity.getCreatedAt() : task.getCreatedAt());
        taskEntity.setCompletedAt("completed".equals(task.getStatus()) ? Instant.now().toString() : null);
//        notificationService.sendNotification(taskEntity.getConsumer().getGlobalId(), notification);
        return taskEntity.copy();
    }

    @Transactional
    public void remove(String taskGlobalId) {
        Optional<Task> byTaskGlobalId = taskRepository.findByTaskGlobalId(taskGlobalId);
        if (byTaskGlobalId.isEmpty()) {
            return;
        }
        Task task = byTaskGlobalId.get();
        Consumer consumer = task.getConsumer().removeTask(taskGlobalId);
        consumerRepository.saveAndFlush(consumer);
//        notificationService.sendNotification(consumer.getGlobalId(), String.format("Задача была удалена [%s]", task.getTitle()));
    }
}
