package com.roberlan.polaris.presentation.dto.response;

import com.roberlan.polaris.core.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        String title,
        String description,
        String assignedTo,
        TaskStatus status,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        LocalDateTime updatedAt
        
) {}