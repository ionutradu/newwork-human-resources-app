package com.newwork.human_resources_app.web.dto.manager;

import com.newwork.human_resources_app.repository.absences.AbsenceStatus;
import com.newwork.human_resources_app.validations.absence.ValidTimeframe;
import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import com.newwork.human_resources_app.web.dto.Timeframe;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidTimeframe
public class ManagerUpdateAbsenceRequestDTO implements Timeframe {

    @FutureOrPresent private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = AbsenceRequestDTO.MAX_REASON_LENGTH)
    private String reason;

    private AbsenceStatus status;
}
