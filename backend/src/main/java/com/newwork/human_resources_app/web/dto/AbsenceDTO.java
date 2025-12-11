package com.newwork.human_resources_app.web.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceDTO {
    private String id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private AbsenceStatusDTO status;

    public enum AbsenceStatusDTO {
        PENDING,
        APPROVED,
        REJECTED
    }
}
