package com.newwork.human_resources_app.web.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    String id;
    String reviewerName;
    String polishedText;
    LocalDateTime createdAt;
}
