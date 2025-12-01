package com.taskflow.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdateDTO {

    private String title;

    private String description;

    private String priority;

    private LocalDate dueDate;

    private Boolean completed;
}
