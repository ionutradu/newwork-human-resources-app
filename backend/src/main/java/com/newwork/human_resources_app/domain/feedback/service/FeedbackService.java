package com.newwork.human_resources_app.domain.feedback.service;

import com.newwork.human_resources_app.domain.feedback.repository.Feedback;
import com.newwork.human_resources_app.domain.feedback.repository.FeedbackRepository;
import com.newwork.human_resources_app.event.kafka.KafkaEventProducerService;
import com.newwork.human_resources_app.shared.dto.FeedbackRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackAiService aiService;
    private final KafkaEventProducerService kafkaEventProducerService;

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
}
