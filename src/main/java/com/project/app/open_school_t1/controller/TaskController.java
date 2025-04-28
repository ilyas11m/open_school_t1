package com.project.app.open_school_t1.controller;

import com.project.app.open_school_t1.dto.TaskDTO;
import com.project.app.open_school_t1.entity.Task;
import com.project.app.open_school_t1.service.inf.TaskService;
import com.project.app.open_school_t1.util.TaskNotFoundException;

import static com.project.app.open_school_t1.dto.TaskDTO.toEntity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {


    private final TaskService taskService;

    TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            TaskDTO task = TaskDTO.toDTO(taskService.getById(id));
            return ResponseEntity.ok(task);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(TaskDTO::toDTO)
                .toList();
    }

    @PostMapping
    public TaskDTO addTask(@RequestBody TaskDTO taskDto) {
        return TaskDTO.toDTO(taskService.addTask(toEntity(taskDto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task newTask) {
        try {
            TaskDTO updatedTask = TaskDTO.toDTO(taskService.update(id, newTask));
            return ResponseEntity.ok(updatedTask);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            TaskDTO deletedTask = TaskDTO.toDTO(taskService.deleteTask(id));
            return ResponseEntity.ok(deletedTask);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
