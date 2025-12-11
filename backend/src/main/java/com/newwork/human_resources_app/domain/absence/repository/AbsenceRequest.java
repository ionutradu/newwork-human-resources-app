package com.newwork.human_resources_app.domain.absence.repository;

import com.newwork.human_resources_app.shared.dto.absence.AbsenceRequestDTO;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("absence_requests")
public class AbsenceRequest {

    @Id private String id;

    private String employeeId;
    private String processedBy;

    private LocalDate startDate;
    private LocalDate endDate;

    @Size(max = AbsenceRequestDTO.MAX_REASON_LENGTH)
    private String reason;

    private AbsenceStatus status = AbsenceStatus.PENDING;

    private Instant processingDate;
    private Instant requestedAt = Instant.now();
}
