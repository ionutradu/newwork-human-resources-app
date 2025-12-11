package com.newwork.human_resources_app.event.kafka;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceStatus;
import java.time.Instant;

public record AbsenceCreatedEvent(
        EventType eventType,
        Instant timestamp,
        String absenceId,
        String employeeId,
        AbsenceStatus status) {
    public AbsenceCreatedEvent(String absenceId, String employeeId) {
        this(
                EventType.ABSENCE_CREATED,
                Instant.now(),
                absenceId,
                employeeId,
                AbsenceStatus.PENDING);
    }
}
