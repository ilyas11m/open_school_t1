package com.project.app.open_school_t1.mapper;

import com.project.app.open_school_t1.dto.TaskDTO;
import com.project.app.open_school_t1.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskDTO toDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .status(task.getStatus())
                .build();
    }

    public Task toEntity(TaskDTO taskDto) {
        Task task = new Task();

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setUserId(taskDto.getUserId());
        task.setStatus(taskDto.getStatus());

        return task;
    }
}
