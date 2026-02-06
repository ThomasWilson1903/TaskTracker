package ru.twilson.tasktracker.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.twilson.tasktracker.entity.Consumer;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.model.CreateTaskRequest;
import ru.twilson.tasktracker.model.TaskNote;
import ru.twilson.tasktracker.model.TaskNoteExecutor;
import ru.twilson.tasktracker.model.UpdateTaskRequest;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "taskGlobalId", ignore = true)
    Task toTask(CreateTaskRequest taskNote);

    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "taskGlobalId", ignore = true)
    Task toTask(UpdateTaskRequest updateTaskRequest);

    @Mapping(target = "id", source = "taskGlobalId")
    TaskNote toTaskNote(Task task);

    List<TaskNote> toTaskNotes(List<Task> tasks);

    @Mapping(target = "id", source = "globalId")
    @Mapping(target = "username", source = "username")
    TaskNoteExecutor consumerToTaskNoteExecutor(Consumer cumer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "globalId", source = "id")
    @Mapping(target = "username", source = "username")
    Consumer taskNoteExecutorToConsumer(TaskNoteExecutor taskNoteExecutor);
}
