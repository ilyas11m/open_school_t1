package com.project.app.open_school_t1.service;

import com.project.app.open_school_t1.util.TaskNotFoundException;
import com.project.app.open_school_t1.entity.Task;
import com.project.app.open_school_t1.repository.TaskRepository;
import com.project.app.open_school_t1.service.inf.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImplementation implements TaskService {

    private final TaskRepository taskRepository;

    TaskServiceImplementation(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Override
    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task update(Long id, Task newTask) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setTitle(newTask.getTitle());
                    task.setDescription(newTask.getDescription());
                    task.setUserId(newTask.getUserId());
                    return taskRepository.save(task);
                })
                .orElseGet(() -> taskRepository.save(newTask));
    }

    @Override
    public Task deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        taskRepository.deleteById(id);
        return task;
    }
}
