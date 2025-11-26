package com.roberlan.polaris.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        String assignedTo,
        String status
) {}