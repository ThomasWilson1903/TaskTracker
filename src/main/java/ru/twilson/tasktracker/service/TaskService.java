package ru.twilson.tasktracker.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.model.UpdateTaskRequest;
import ru.twilson.tasktracker.repository.ConsumerRepository;
import ru.twilson.tasktracker.repository.TaskRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ConsumerService consumerService;
    private final ConsumerRepository consumerRepository;

    public Task getTaskByIdTask(String taskId) {
        return taskRepository.findByTaskGlobalId(taskId).orElseThrow(() -> new EntityNotFoundException("Task not found"));
    }

    public List<Task> getTaskByIdConsumer(String consumerGlobalId) {
        if (consumerGlobalId == null) {
            throw new NullPointerException("consumerGlobalId is null");
        }
        Optional<Consumer> consumer = consumerRepository.findByGlobalId(consumerGlobalId);
        if (consumer.isEmpty()) {
            return List.of();// TODO Exception
        } else return consumer.get().getTasks();
    }

    public void add(String consumerGlobalId, Task task) {
        if (consumerGlobalId == null || task == null) {
            throw new NullPointerException("null");
        }

        Optional<Consumer> consumerOptional = consumerRepository.findByGlobalId(consumerGlobalId);
        task.setTaskGlobalId(UUID.randomUUID().toString());
        task.setCreatedAt(Instant.now().toString());
        task.setUpdatedAt(Instant.now().toString());
        if (consumerOptional.isEmpty()) {
            Consumer consumer = Consumer.builder().globalId(consumerGlobalId).build();
            task.setConsumer(consumer);
            consumer.addTask(task);
            consumerService.add(consumer);
        } else {
            Consumer consumer = consumerOptional.get();
            task.setConsumer(consumer);
            consumerService.add(consumer.addTask(task));
        }
    }

    @Transactional
    public Task update(Task task) {
        Optional<Task> byTaskGlobalId = taskRepository.findByTaskGlobalId(task.getTaskGlobalId());
        if (byTaskGlobalId.isEmpty()) {
            throw new EntityNotFoundException("Task not found");
        }
        Task taskEntity = byTaskGlobalId.get();
        taskEntity.setUpdatedAt(Instant.now().toString());
        taskEntity.setTitle(task.getTitle());
        taskEntity.setDescription(task.getDescription());
        taskEntity.setPriority(task.getPriority());
        taskEntity.setDueDate(task.getDueDate());
        taskEntity.setCompletedAt(task.getCompletedAt());
        return taskEntity;
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
    }
}
