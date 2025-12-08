package com.newwork.human_resources_app.service.feedback;

import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.newwork.human_resources_app.web.dto.FeedbackRequestDTO.MAX_FEEDBACK_LENGTH;

@Service
@RequiredArgsConstructor
public class FeedbackAiService {

    public String polishFeedback(@Max(MAX_FEEDBACK_LENGTH) String feedback) {
        // TODO
        return feedback;
    }

}
