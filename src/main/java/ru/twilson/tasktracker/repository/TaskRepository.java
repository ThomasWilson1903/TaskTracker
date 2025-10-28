package ru.twilson.tasktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.twilson.tasktracker.entity.Task;

import java.util.Optional;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByTaskGlobalId(String taskGlobalId);
}
