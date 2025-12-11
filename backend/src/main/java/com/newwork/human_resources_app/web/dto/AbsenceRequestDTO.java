package com.newwork.human_resources_app.web.dto;

import com.newwork.human_resources_app.validations.absence.ValidTimeframe;
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
public class AbsenceRequestDTO {

    public static final int MAX_REASON_LENGTH = 1000;

    @NotNull
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull private LocalDate endDate;

    @NotBlank
    @Size(max = MAX_REASON_LENGTH)
    private String reason;
}
