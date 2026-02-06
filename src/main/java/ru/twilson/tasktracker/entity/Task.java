package ru.twilson.tasktracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue
    private long taskId;

    @Column(unique = true, nullable = false)
    private String taskGlobalId;
    private String title;
    private String description;
    private String priority;
    private String dueDate;
    private String createdAt;
    private String updatedAt;
    private String completedAt;
    private String status;
    private boolean isDeleted = false;

    @ManyToOne(cascade = CascadeType.ALL)
    private Consumer creator;

    @ManyToOne(cascade = CascadeType.ALL)
    private Consumer executor;


    public Task copy() {
        return Task.builder()
                .taskId(taskId)
                .taskGlobalId(taskGlobalId)
                .title(title)
                .description(description)
                .priority(priority)
                .dueDate(dueDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .completedAt(completedAt)
                .status(status)
                .creator(creator)
                .executor(executor)
                .build();
    }
}
