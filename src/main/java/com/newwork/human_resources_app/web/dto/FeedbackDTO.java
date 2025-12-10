package com.newwork.human_resources_app.web.dto;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class FeedbackDTO {
    String id;
    String reviewerEmployeeId;
    String originalText;
    String polishedText;
    LocalDateTime createdAt;
}