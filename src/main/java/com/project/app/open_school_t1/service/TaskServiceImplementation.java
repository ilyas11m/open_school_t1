package com.project.app.open_school_t1.service;

import com.project.app.open_school_t1.exception.TaskNotFoundException;
import com.project.app.open_school_t1.entity.Task;
import com.project.app.open_school_t1.kafka.producer.KafkaTaskProducer;
import com.project.app.open_school_t1.mapper.TaskMapper;
import com.project.app.open_school_t1.repository.TaskRepository;
import com.project.app.open_school_t1.service.inf.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImplementation implements TaskService {

    private final TaskRepository taskRepository;
    private final KafkaTaskProducer kafkaTaskProducer;
    private final TaskMapper taskMapper;

    TaskServiceImplementation(TaskRepository taskRepository, KafkaTaskProducer kafkaTaskProducer, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.kafkaTaskProducer = kafkaTaskProducer;
        this.taskMapper = taskMapper;
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
                    task.setStatus(newTask.getStatus());
                    kafkaTaskProducer.send(taskMapper.toDTO(task));

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
