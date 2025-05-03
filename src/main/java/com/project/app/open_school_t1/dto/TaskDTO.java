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

}