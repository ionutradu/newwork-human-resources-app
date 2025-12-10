package com.newwork.human_resources_app.service.events;

import java.time.Instant;

public record FeedbackAddedEvent(
        EventType eventType,
        Instant timestamp,
        String feedbackId,
        String reviewerId,
        String targetEmployeeId
) {
    public FeedbackAddedEvent(String feedbackId, String reviewerId, String targetEmployeeId) {
        this(EventType.FEEDBACK_ADDED, Instant.now(), feedbackId, reviewerId, targetEmployeeId);
    }
}