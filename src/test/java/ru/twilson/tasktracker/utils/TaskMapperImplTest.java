package ru.twilson.tasktracker.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.twilson.tasktracker.entity.Task;
import ru.twilson.tasktracker.model.CreateTaskRequest;
import ru.twilson.tasktracker.model.TaskNote;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TaskMapperImplTest {

    @InjectMocks
    private TaskMapperImpl taskMapper;

    @Test
    @DisplayName("Мапинг dto -> entity")
    void toTask() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest("", "")
                .description("description")
                .dueDate(Instant.now().toString())
                .priority("1");
        Task task = taskMapper.toTask(createTaskRequest);
        Assertions.assertEquals(task.getTitle(), createTaskRequest.getTitle());
        Assertions.assertEquals(task.getDescription(), createTaskRequest.getDescription());
        Assertions.assertEquals(task.getDueDate(), createTaskRequest.getDueDate());
        Assertions.assertEquals(task.getPriority(), createTaskRequest.getPriority());
    }

    @Test
    @DisplayName("Мапинг dto(null) -> entity ")
    void toTaskNull() {
        Task task = taskMapper.toTask((CreateTaskRequest) null);
        Assertions.assertNull(task);
    }


    @Test
    @DisplayName("Мапинг entity -> dto")
    void toTaskNote() {
        Task task = Task.builder()
                .taskGlobalId("taskGlobalId")
                .title("title")
                .description("description")
                .priority("priority")
                .dueDate(Instant.now().toString())
                .createdAt(Instant.now().toString())
                .updatedAt(Instant.now().toString())
                .completedAt(Instant.now().toString())
                .build();
        TaskNote taskNote = taskMapper.toTaskNote(task);
        Assertions.assertEquals(task.getTitle(), taskNote.getTitle());
        Assertions.assertEquals(task.getDescription(), taskNote.getDescription());
        Assertions.assertEquals(task.getDueDate(), taskNote.getDueDate());
        Assertions.assertEquals(task.getPriority(), taskNote.getPriority());
        Assertions.assertEquals(task.getCreatedAt(), taskNote.getCreatedAt());
        Assertions.assertEquals(task.getUpdatedAt(), taskNote.getUpdatedAt());
        Assertions.assertEquals(task.getCompletedAt(), taskNote.getCompletedAt());
        Assertions.assertEquals(task.getTaskGlobalId(), taskNote.getId());
    }

    @Test
    @DisplayName("Мапинг entity(null) -> dto")
    void toTaskNoteNull() {
        TaskNote taskNote = taskMapper.toTaskNote(null);
        Assertions.assertNull(taskNote);
    }

    @Test
    @DisplayName("Мапинг entity-s -> dto-s")
    void toTaskNotes() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(Task.builder()
                    .taskGlobalId("taskGlobalId" + i)
                    .title("title" + i)
                    .description("description" + i)
                    .priority("priority" + i)
                    .dueDate(Instant.now().toString() + i)
                    .createdAt(Instant.now().toString() + i)
                    .updatedAt(Instant.now().toString() + i)
                    .completedAt(Instant.now().toString() + i)
                    .build());
        }
        List<TaskNote> taskNotes = taskMapper.toTaskNotes(tasks);
        for (int i = 0; i < 10; i++) {
            TaskNote taskNote = taskNotes.get(i);
            Task task = tasks.get(i);
            Assertions.assertEquals(task.getTitle(), taskNote.getTitle());
            Assertions.assertEquals(task.getDescription(), taskNote.getDescription());
            Assertions.assertEquals(task.getDueDate(), taskNote.getDueDate());
            Assertions.assertEquals(task.getPriority(), taskNote.getPriority());
            Assertions.assertEquals(task.getCreatedAt(), taskNote.getCreatedAt());
            Assertions.assertEquals(task.getUpdatedAt(), taskNote.getUpdatedAt());
            Assertions.assertEquals(task.getCompletedAt(), taskNote.getCompletedAt());
            Assertions.assertEquals(task.getTaskGlobalId(), taskNote.getId());
        }
    }

    @Test
    @DisplayName("Мапинг entity-s(null) -> dto-s")
    void toTaskNotesNull() {
        List<TaskNote> taskNotes = taskMapper.toTaskNotes(null);
        Assertions.assertNull(taskNotes);
    }
}