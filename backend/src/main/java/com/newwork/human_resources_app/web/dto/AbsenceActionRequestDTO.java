package com.newwork.human_resources_app.web.dto;

import jakarta.validation.constraints.NotNull;

public record AbsenceActionRequestDTO(
        @NotNull(message = "Action must be provided (APPROVE or REJECT)")
                AbsenceActionDTO action) {}
