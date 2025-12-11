package com.newwork.human_resources_app.shared.dto.manager;

import com.newwork.human_resources_app.shared.dto.FeedbackRequestDTO;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerUpdateFeedbackDTO {

    @Size(max = FeedbackRequestDTO.MAX_FEEDBACK_LENGTH)
    private String originalText;

    @Size(max = FeedbackRequestDTO.MAX_FEEDBACK_LENGTH)
    private String polishedText;
}
