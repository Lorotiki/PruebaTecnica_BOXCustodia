package com.taskflow.api.dto;

import com.taskflow.api.entity.TaskPriority;
import com.taskflow.api.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TaskRequest(
    @NotBlank String title,
    String description,
    @NotNull TaskStatus status,
    @NotNull TaskPriority priority,
    LocalDate dueDate,
    Long assignedToId,
    @NotNull Long createdById
) {
}
