package com.newwork.human_resources_app.shared.dto.absence;

import com.newwork.human_resources_app.domain.absence.validation.ValidTimeframe;
import com.newwork.human_resources_app.shared.dto.Timeframe;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidTimeframe
public class AbsenceRequestDTO implements Timeframe {

    public static final int MAX_REASON_LENGTH = 1000;

    @NotNull
    @FutureOrPresent(message = "cannot be in the past")
    private LocalDate startDate;

    @NotNull private LocalDate endDate;

    @NotBlank
    @Size(max = MAX_REASON_LENGTH)
    private String reason;
}
