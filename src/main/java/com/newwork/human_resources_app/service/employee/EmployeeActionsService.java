package com.newwork.human_resources_app.service.employee;

import com.newwork.human_resources_app.repository.absences.AbsenceRepository;
import com.newwork.human_resources_app.repository.absences.AbsenceStatus;
import com.newwork.human_resources_app.repository.feedback.Feedback;
import com.newwork.human_resources_app.repository.feedback.FeedbackRepository;
import com.newwork.human_resources_app.service.events.KafkaEventProducerService;
import com.newwork.human_resources_app.service.feedback.FeedbackAiService;
import com.newwork.human_resources_app.service.mapper.AbsenceMapper;
import com.newwork.human_resources_app.web.dto.AbsenceActionRequestDTO;
import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import com.newwork.human_resources_app.web.dto.FeedbackRequestDTO;
import com.newwork.human_resources_app.web.exceptions.NotFoundException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeActionsService {

    private final FeedbackRepository feedbackRepository;
    private final AbsenceRepository absenceRepository;

    private final FeedbackAiService aiService;

    private final KafkaEventProducerService kafkaEventProducerService;

    private final AbsenceMapper absenceMapper;

    @Transactional
    public Feedback leaveFeedback(
            String targetEmployeeId, String reviewerEmployeeId, FeedbackRequestDTO dto) {
        var polishedText = aiService.polishFeedback(dto.getText());

        var feedback = new Feedback();
        feedback.setTargetEmployeeId(targetEmployeeId);
        feedback.setReviewerEmployeeId(reviewerEmployeeId);
        feedback.setOriginalText(dto.getText());
        feedback.setPolishedText(polishedText);

        var savedFeedback = feedbackRepository.save(feedback);

        kafkaEventProducerService.sendFeedbackAddedEvent(
                reviewerEmployeeId, targetEmployeeId, savedFeedback.getId());

        return savedFeedback;
    }

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
