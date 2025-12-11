package com.newwork.human_resources_app.web.dto.manager;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.newwork.human_resources_app.web.dto.FeedbackRequestDTO.MAX_FEEDBACK_LENGTH;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerUpdateFeedbackDTO {
    
    @Size(max = MAX_FEEDBACK_LENGTH)
    private String originalText;
    
    @Size(max = MAX_FEEDBACK_LENGTH)
    private String polishedText;
}