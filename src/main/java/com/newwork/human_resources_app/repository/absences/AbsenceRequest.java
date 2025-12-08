package com.newwork.human_resources_app.repository.absences;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

import static com.newwork.human_resources_app.web.dto.AbsenceRequestDTO.MAX_REASON_LENGTH;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("absence_requests")
public class AbsenceRequest {

    @Id
    private String id;
    
    private String employeeId;

    private LocalDate startDate;
    private LocalDate endDate;

    @Max(MAX_REASON_LENGTH)
    private String reason;

    private AbsenceStatus status = AbsenceStatus.PENDING;

    private Instant requestedAt = Instant.now();
}