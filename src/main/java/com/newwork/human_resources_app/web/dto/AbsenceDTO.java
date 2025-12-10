package com.newwork.human_resources_app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceDTO {
    private String id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
}