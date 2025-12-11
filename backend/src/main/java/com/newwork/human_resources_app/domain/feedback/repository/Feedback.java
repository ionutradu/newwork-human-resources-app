package com.newwork.human_resources_app.domain.feedback.repository;

import static com.newwork.human_resources_app.shared.dto.FeedbackRequestDTO.MAX_FEEDBACK_LENGTH;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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
@Document("feedbacks")
public class Feedback {
    @Id private String id;

    private String targetEmployeeId;
    private String reviewerEmployeeId;

    @Size(max = MAX_FEEDBACK_LENGTH)
    private String originalText;

    @Size(max = MAX_FEEDBACK_LENGTH)
    private String polishedText;

    private LocalDateTime createdAt = LocalDateTime.now();
}
