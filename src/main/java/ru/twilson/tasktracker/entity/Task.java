package ru.twilson.tasktracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
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

    @ManyToOne(cascade = CascadeType.ALL)
    private Consumer consumer;
}
