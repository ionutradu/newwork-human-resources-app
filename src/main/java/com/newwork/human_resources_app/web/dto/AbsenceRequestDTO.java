package com.newwork.human_resources_app.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AbsenceRequestDTO {

    public static final int MAX_REASON_LENGTH = 1000;

    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;

    @Max(MAX_REASON_LENGTH)
    private String reason;

}
