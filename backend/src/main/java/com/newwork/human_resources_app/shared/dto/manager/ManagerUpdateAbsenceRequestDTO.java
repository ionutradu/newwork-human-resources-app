package com.newwork.human_resources_app.shared.dto.manager;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceStatus;
import com.newwork.human_resources_app.domain.absence.validation.ValidTimeframe;
import com.newwork.human_resources_app.shared.dto.Timeframe;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceRequestDTO;
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
