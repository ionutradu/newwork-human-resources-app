package com.newwork.human_resources_app.unit_tests.feedback;

import com.newwork.human_resources_app.domain.feedback.repository.Feedback;
import com.newwork.human_resources_app.domain.feedback.repository.FeedbackRepository;
import com.newwork.human_resources_app.domain.feedback.service.FeedbackAiService;
import com.newwork.human_resources_app.domain.feedback.service.FeedbackService;
import com.newwork.human_resources_app.event.kafka.KafkaEventProducerService;
import com.newwork.human_resources_app.shared.dto.FeedbackDTO;
import com.newwork.human_resources_app.shared.dto.FeedbackRequestDTO;
import com.newwork.human_resources_app.shared.mapper.FeedbackMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private FeedbackMapper feedbackMapper;
    @Mock
    private FeedbackAiService feedbackAiService;
    @Mock
    private KafkaEventProducerService kafkaEventProducerService;

    @InjectMocks
    private FeedbackService feedbackService;

    private FeedbackRequestDTO requestDTO;
    private Feedback feedbackEntity;
    private FeedbackDTO expectedDTO;
    private final String RAW_CONTENT = "Raw feedback content";
    private final String POLISHED_CONTENT = "Polished feedback content from AI";

    @BeforeEach
    void setUp() {
        requestDTO = new FeedbackRequestDTO(RAW_CONTENT);
        
        feedbackEntity = new Feedback();
        feedbackEntity.setOriginalText(RAW_CONTENT);
        feedbackEntity.setId("fdb101");
        
        expectedDTO = new FeedbackDTO();
        expectedDTO.setPolishedText(POLISHED_CONTENT);
    }

    @Test
    @DisplayName("Add feedback with text polishing via AI service")
    void addFeedback_Success_WithPolishing() {
        // Arrange
        when(feedbackAiService.polishFeedback(RAW_CONTENT)).thenReturn(POLISHED_CONTENT);
        
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(i -> {
            Feedback saved = i.getArgument(0);
            saved.setPolishedText(POLISHED_CONTENT);
            return saved;
        });
        
        // Act
        feedbackService.leaveFeedback("employee", "coworker", requestDTO);

        // Assert
        verify(feedbackAiService, times(1)).polishFeedback(RAW_CONTENT);
        
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
        
        verify(kafkaEventProducerService, times(1)).sendFeedbackAddedEvent(any(), any(), any());
    }

}