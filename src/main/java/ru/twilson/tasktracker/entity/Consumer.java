package ru.twilson.tasktracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<Task> tasks;

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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Consumer consumer = (Consumer) o;
        return id == consumer.id && Objects.equals(globalId, consumer.globalId) && Objects.equals(username, consumer.username) && Objects.equals(password, consumer.password) && Objects.equals(firstName, consumer.firstName) && Objects.equals(lastName, consumer.lastName) && Objects.equals(tasks, consumer.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, globalId, username, password, firstName, lastName, tasks);
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
