package com.newwork.human_resources_app.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    String id;
    String reviewerEmployeeId;
    String originalText;
    String polishedText;
    LocalDateTime createdAt;
}