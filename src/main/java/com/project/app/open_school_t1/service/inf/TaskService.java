package com.project.app.open_school_t1.service.inf;

import com.project.app.open_school_t1.entity.Task;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();

    Task getById(Long id);

    Task addTask(Task task);

    Task update(Long id, Task newTask);

    Task deleteTask(Long id);
}
