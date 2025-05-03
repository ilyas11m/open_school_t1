package com.project.app.open_school_t1.mapper;

import com.project.app.open_school_t1.dto.TaskDTO;
import com.project.app.open_school_t1.entity.Task;

public class TaskMapper {
    public static TaskDTO toDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .userId(task.getUserId())
                .build();
    }

    public static Task toEntity(TaskDTO taskDto) {
        Task task = new Task();

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setUserId(taskDto.getUserId());

        return task;
    }
}
