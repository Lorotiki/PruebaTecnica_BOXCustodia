package com.taskflow.api.dto;

import com.taskflow.api.entity.TaskPriority;
import com.taskflow.api.entity.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status,
    TaskPriority priority,
    LocalDate dueDate,
    Instant createdAt,
    Instant updatedAt,
    UserResponse assignedTo,
    UserResponse createdBy
) {
}
