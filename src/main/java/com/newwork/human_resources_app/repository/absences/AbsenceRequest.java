package com.newwork.human_resources_app.repository.absences;

import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("absence_requests")
public class AbsenceRequest {

    @Id
    private String id;
    
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