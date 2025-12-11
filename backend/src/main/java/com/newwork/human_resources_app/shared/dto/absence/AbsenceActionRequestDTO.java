package com.newwork.human_resources_app.shared.dto.absence;

import jakarta.validation.constraints.NotNull;

public record AbsenceActionRequestDTO(
        @NotNull(message = "Action must be provided (APPROVE or REJECT)")
                AbsenceActionDTO action) {}
