package com.newwork.human_resources_app.web.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class AbsenceResponseDTO {
    String id;
    LocalDate startDate;
    LocalDate endDate;
    String reason;
}