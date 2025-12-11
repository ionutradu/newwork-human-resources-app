package com.newwork.human_resources_app.shared.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDTO {

    public static final int MAX_FEEDBACK_LENGTH = 1000;

    @Size(max = MAX_FEEDBACK_LENGTH)
    private String text;
}
