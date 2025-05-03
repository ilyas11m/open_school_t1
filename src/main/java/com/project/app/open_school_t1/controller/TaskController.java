package com.project.app.open_school_t1.controller;

import com.project.app.open_school_t1.dto.TaskDTO;
import com.project.app.open_school_t1.entity.Task;
import com.project.app.open_school_t1.mapper.TaskMapper;
import com.project.app.open_school_t1.service.inf.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.project.app.open_school_t1.mapper.TaskMapper.toEntity;

@RestController
@RequestMapping("/tasks")
public class TaskController {


    private final TaskService taskService;

    TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public TaskDTO getTaskById(@PathVariable Long id) {
        return TaskMapper.toDTO(taskService.getById(id));
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public TaskDTO addTask(@RequestBody TaskDTO taskDto) {
        return TaskMapper.toDTO(taskService.addTask(toEntity(taskDto)));
    }

    @PutMapping("/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public TaskDTO updateTask(@PathVariable Long id, @RequestBody Task newTask) {
        return TaskMapper.toDTO(taskService.update(id, newTask));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
