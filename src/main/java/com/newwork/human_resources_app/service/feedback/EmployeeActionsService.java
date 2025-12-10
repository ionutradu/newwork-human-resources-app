package com.newwork.human_resources_app.service.feedback;

import com.newwork.human_resources_app.repository.absences.AbsenceRepository;
import com.newwork.human_resources_app.repository.feedback.Feedback;
import com.newwork.human_resources_app.repository.feedback.FeedbackRepository;
import com.newwork.human_resources_app.service.mapper.AbsenceMapper;
import com.newwork.human_resources_app.web.dto.AbsenceRequestDTO;
import com.newwork.human_resources_app.web.dto.FeedbackRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeActionsService {

    private final FeedbackRepository feedbackRepository;
    private final AbsenceRepository absenceRepository;
    private final FeedbackAiService aiService;
    private final AbsenceMapper absenceMapper;

    @Transactional
    public Feedback leaveFeedback(String targetUserId, String reviewerUserId, FeedbackRequestDTO dto) {
        var polishedText = aiService.polishFeedback(dto.getText());
        
        var feedback = new Feedback();
        feedback.setTargetEmployeeId(targetUserId);
        feedback.setReviewerEmployeeId(reviewerUserId);
        feedback.setOriginalText(dto.getText());
        feedback.setPolishedText(polishedText);
        
        return feedbackRepository.save(feedback);
    }

    public void requestAbsence(String employeeId, AbsenceRequestDTO dto) {
        var absenceRequest = absenceMapper.toEntity(dto, employeeId);
        absenceRepository.save(absenceRequest);
    }
}