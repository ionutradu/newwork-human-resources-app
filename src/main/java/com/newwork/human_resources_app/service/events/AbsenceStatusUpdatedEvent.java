package com.newwork.human_resources_app.service.events;

import com.newwork.human_resources_app.repository.absences.AbsenceStatus;
import java.time.Instant;

public record AbsenceStatusUpdatedEvent(
        EventType eventType,
        Instant timestamp,
        String absenceId,
        String employeeId,
        AbsenceStatus newStatus,
        String processedByManagerId) {
    public AbsenceStatusUpdatedEvent(
            String absenceId,
            String employeeId,
            AbsenceStatus newStatus,
            String processedByManagerId) {
        this(
                EventType.ABSENCE_STATUS_UPDATED,
                Instant.now(),
                absenceId,
                employeeId,
                newStatus,
                processedByManagerId);
    }
}
