package com.newwork.human_resources_app.service.events;

import com.newwork.human_resources_app.repository.absences.AbsenceStatus;
import java.time.Instant;

public record AbsenceCreatedEvent(
        EventType eventType,
        Instant timestamp,
        String absenceId,
        String employeeId,
        AbsenceStatus status 
) {
    public AbsenceCreatedEvent(String absenceId, String employeeId) {
        this(EventType.ABSENCE_CREATED, Instant.now(), absenceId, employeeId, AbsenceStatus.PENDING);
    }
}