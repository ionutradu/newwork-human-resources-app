package com.newwork.human_resources_app.repository.feedback;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import static com.newwork.human_resources_app.web.dto.FeedbackRequestDTO.MAX_FEEDBACK_LENGTH;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("feedbacks")
public class Feedback {
    @Id
    private String id;
    
    private String targetEmployeeId;
    private String reviewerEmployeeId;

    @Max(MAX_FEEDBACK_LENGTH)
    private String originalText;
    @Max(MAX_FEEDBACK_LENGTH)
    private String polishedText;

    private Instant createdAt = Instant.now();
}