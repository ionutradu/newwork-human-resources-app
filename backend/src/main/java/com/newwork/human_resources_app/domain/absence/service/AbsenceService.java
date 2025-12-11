package com.newwork.human_resources_app.domain.absence.service;

import com.newwork.human_resources_app.domain.absence.repository.AbsenceRepository;
import com.newwork.human_resources_app.domain.absence.repository.AbsenceStatus;
import com.newwork.human_resources_app.event.kafka.KafkaEventProducerService;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceActionRequestDTO;
import com.newwork.human_resources_app.shared.dto.absence.AbsenceRequestDTO;
import com.newwork.human_resources_app.shared.exception.NotFoundException;
import com.newwork.human_resources_app.shared.mapper.AbsenceMapper;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AbsenceService {

    private final AbsenceRepository absenceRepository;
    private final AbsenceMapper absenceMapper;
    private final KafkaEventProducerService kafkaEventProducerService;

    @Transactional
    public void requestAbsence(String employeeId, AbsenceRequestDTO dto) {
        var absenceRequest = absenceMapper.toEntity(dto, employeeId);
        var savedAbsenceRequest = absenceRepository.save(absenceRequest);
        kafkaEventProducerService.sendAbsenceCreatedEvent(employeeId, savedAbsenceRequest.getId());
    }

    @Transactional
    public void processAbsenceRequest(
            String absenceId, String managerId, AbsenceActionRequestDTO requestDTO) {
        var absence =
                absenceRepository
                        .findById(absenceId)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                "Absence Request not found with ID: " + absenceId));

        if (absence.getStatus() != AbsenceStatus.PENDING) {
            log.error("Absence %s not in 'PENDING' status".formatted(absenceId));
            return;
        }

        var newStatus =
                switch (requestDTO.action()) {
                    case APPROVE -> AbsenceStatus.APPROVED;
                    case REJECT -> AbsenceStatus.REJECTED;
                };

        absence.setStatus(newStatus);
        absence.setProcessedBy(managerId);
        absence.setProcessingDate(Instant.now());

        absenceRepository.save(absence);

        kafkaEventProducerService.sendAbsenceUpdatedEvent(
                absence.getEmployeeId(), absence.getId(), newStatus, managerId);
    }
}
