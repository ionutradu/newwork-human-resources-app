package com.newwork.human_resources_app.domain.feedback.service;

import static com.newwork.human_resources_app.shared.dto.FeedbackRequestDTO.MAX_FEEDBACK_LENGTH;

import com.newwork.human_resources_app.core.client.hugging_face.HuggingFaceChatClient;
import com.newwork.human_resources_app.core.client.hugging_face.dto.HFChatRequest;
import com.newwork.human_resources_app.core.client.hugging_face.dto.HFMessage;
import com.newwork.human_resources_app.core.config.HuggingFaceProperties;
import io.jsonwebtoken.lang.Strings;
import jakarta.validation.constraints.Max;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackAiService {

    private final HuggingFaceChatClient chatClient;
    private final HuggingFaceProperties huggingFaceProperties;

    private static final String AI_PROMPT_TEMPLATE =
            """
                Refine and improve the following feedback text.
                Ensure it is constructive, professional, and polite.
                Do not add any additional explanations; return only the refined text.

                Original text: %s
                """;

    // In a production app, another backoff policy should be used (e.g. ExponentialBackOffPolicy).
    // For the sake of the integration test, there are only 5 attempts of 100ms fixed retry time.
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 100))
    public String polishFeedback(@Max(MAX_FEEDBACK_LENGTH) String feedback) {
        var input = AI_PROMPT_TEMPLATE.formatted(feedback);

        var response =
                chatClient.generateChatCompletion(
                        new HFChatRequest(
                                huggingFaceProperties.getModel(),
                                List.of(new HFMessage("user", input))));

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return Strings.EMPTY;
        }

        var firstChoice = response.choices().get(0);

        if (firstChoice == null || firstChoice.message() == null) {
            return Strings.EMPTY;
        }

        var content = firstChoice.message().content();

        return content != null ? content.trim() : Strings.EMPTY;
    }

    @Recover
    public String recoverPolishFeedback(Exception e, String feedback) {
        log.error(
                "Feedback polishing with AI failed after all retries. Returning original feedback.",
                e);

        return feedback;
    }
}
