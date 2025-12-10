package com.newwork.human_resources_app.service.feedback;

import com.newwork.human_resources_app.client.hugging_face.HuggingFaceChatClient;
import com.newwork.human_resources_app.client.hugging_face.dto.HFChatRequest;
import com.newwork.human_resources_app.client.hugging_face.dto.HFMessage;
import com.newwork.human_resources_app.config.HuggingFaceProperties;
import io.jsonwebtoken.lang.Strings;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.newwork.human_resources_app.web.dto.FeedbackRequestDTO.MAX_FEEDBACK_LENGTH;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackAiService {

    private final HuggingFaceChatClient chatClient;
    private final HuggingFaceProperties huggingFaceProperties;

    private static final String AI_PROMPT_TEMPLATE = """
                Refine and improve the following feedback text.
                Ensure it is constructive, professional, and polite.
                Do not add any additional explanations; return only the refined text.
                                 
                Original text: %s
                """;

    public String polishFeedback(@Max(MAX_FEEDBACK_LENGTH) String feedback) {

        var input = AI_PROMPT_TEMPLATE.formatted(feedback);

        try {
            var response = chatClient.generateChatCompletion(new HFChatRequest(huggingFaceProperties.getModel(), List.of(new HFMessage("user", input))));

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                return Strings.EMPTY;
            }

            var firstChoice = response.choices().get(0);

            if (firstChoice == null || firstChoice.message() == null) {
                return Strings.EMPTY;
            }

            var content = firstChoice.message().content();

            return content != null ? content.trim() : Strings.EMPTY;
        } catch (Exception e) {
            log.error("Feedback polishing with AI failed.", e);
        }

        return feedback;
    }

}
