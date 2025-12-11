package com.newwork.human_resources_app.event.kafka;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// This is a dummy service, show-casing that events could be sent via Kafka to a
// communication-service.
// This communication-service (another microservice) would consume Kafka events and send
// notifications (email, SMS or other type) to interested parties.

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducerService {

    private static final String TOPIC_ABSENCE = "hr.absences";
    private static final String TOPIC_FEEDBACK = "hr.feedback";

    public void sendAbsenceCreatedEvent(String employeeId, String absenceId) {
        var event = new AbsenceCreatedEvent(absenceId, employeeId);

        log.info(
                "--- Kafka Event ({}): Sent {} to topic {}. Payload: {}",
                event.eventType().name(),
                event.getClass().getSimpleName(),
                TOPIC_ABSENCE,
                event);
    }

    public void sendAbsenceUpdatedEvent(
            String employeeId,
            String absenceId,
            AbsenceStatus newStatus,
            String processedByManagerId) {
        var event =
                new AbsenceStatusUpdatedEvent(
                        absenceId, employeeId, newStatus, processedByManagerId);

        log.info(
                "--- Kafka Event ({}): Sent {} to topic {}. Payload: {}",
                event.eventType().name(),
                event.getClass().getSimpleName(),
                TOPIC_ABSENCE,
                event);
    }

    public void sendFeedbackAddedEvent(String reviewerId, String targetId, String feedbackId) {
        var event = new FeedbackAddedEvent(feedbackId, reviewerId, targetId);

        log.info(
                "--- Kafka Event ({}): Sent {} to topic {}. Payload: {}",
                event.eventType().name(),
                event.getClass().getSimpleName(),
                TOPIC_FEEDBACK,
                event);
    }
}
