package com.newwork.human_resources_app.web.dto;

import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class FeedbackRequestDTO {

    public static final int MAX_FEEDBACK_LENGTH = 1000;

    @Max(MAX_FEEDBACK_LENGTH)
    private String text;

}
