package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCreateDTO {
    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String priority;

    private LocalDate dueDate;
}
