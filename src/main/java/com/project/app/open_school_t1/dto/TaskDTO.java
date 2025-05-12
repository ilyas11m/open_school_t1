package com.project.app.open_school_t1.dto;

import com.project.app.open_school_t1.consts.TaskStatus;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private Long userId;
    private TaskStatus status;

}