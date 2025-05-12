package com.project.app.open_school_t1.controller;

import com.project.app.open_school_t1.dto.TaskDTO;
import com.project.app.open_school_t1.entity.Task;
import com.project.app.open_school_t1.mapper.TaskMapper;
import com.project.app.open_school_t1.service.inf.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {


    private final TaskService taskService;
    private final TaskMapper taskMapper;

    TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public TaskDTO getTaskById(@PathVariable Long id) {
        return taskMapper.toDTO(taskService.getById(id));
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public TaskDTO addTask(@RequestBody TaskDTO taskDto) {
        return taskMapper.toDTO(taskService.addTask(taskMapper.toEntity(taskDto)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody Task newTask) {
        return taskMapper.toDTO(taskService.update(id, newTask));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
