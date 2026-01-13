package ru.twilson.tasktracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Consumer {

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true, nullable = false)
    private String globalId;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private boolean enable;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private List<Task> tasks;

    public Consumer copy() {
        return Consumer.builder()
                .id(this.getId())
                .globalId(this.getGlobalId())
                .username(this.getUsername())
                .password(this.getPassword())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .tasks(this.getTasks())
                .enable(this.isEnable())
                .build();
    }

    public Consumer copyPasswordEmpty() {
        return Consumer.builder()
                .id(this.getId())
                .globalId(this.getGlobalId())
                .username(this.getUsername())
                .password(this.getPassword())
                .firstName(this.getFirstName())
                .lastName(this.getLastName())
                .tasks(this.getTasks())
                .enable(this.isEnable())
                .build();
    }

    public Consumer addTask(Task task) {
        if (task == null) {
            return this;
        }
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        tasks.add(task);
        return this;
    }

    public Consumer removeTask(String taskGlobalId) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
            return this;
        }
        tasks.removeIf(task -> task.getTaskGlobalId().equals(taskGlobalId));
        return this;
    }

    @Override
    public String toString() {
        return "Consumer{" +
                "id=" + id +
                ", globalId='" + globalId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", tasks=" + tasks +
                '}';
    }
}
