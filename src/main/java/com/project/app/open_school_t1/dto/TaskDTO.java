package com.project.app.open_school_t1.dto;

import com.project.app.open_school_t1.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private Long userId;

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