package ru.twilson.tasktracker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.twilson.tasktracker.api.TasksApi;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.model.*;
import ru.twilson.tasktracker.service.TaskService;
import ru.twilson.tasktracker.utils.TaskMapper;

@RestController
@RequiredArgsConstructor
public class TaskApiImpl implements TasksApi {

    private final TaskMapper mapper;
    private final TaskService service;

    @Override
    public ResponseEntity<TasksGet200Response> tasksGet(String userId) {
        return new ResponseEntity<>(new TasksGet200Response().data(mapper.toTaskNotes(service.getTaskByIdConsumer(userId))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TaskNote> tasksPost(CreateTaskRequest createTaskRequest) {
        return new ResponseEntity<>(mapper.toTaskNote(service.add(createTaskRequest.getUserId(), mapper.toTask(createTaskRequest))), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> tasksTaskIdDelete(String taskId) {
        service.remove(taskId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TaskNote> tasksTaskIdGet(String taskId) {
        return new ResponseEntity<>(mapper.toTaskNote(service.getTaskByIdTask(taskId)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TaskNote> tasksTaskIdPut(String taskId, UpdateTaskRequest updateTaskRequest) {
        Task task = mapper.toTask(updateTaskRequest);
        task.setTaskGlobalId(taskId);
        return new ResponseEntity<>(mapper.toTaskNote(service.update(task)), HttpStatus.OK);
    }

}
