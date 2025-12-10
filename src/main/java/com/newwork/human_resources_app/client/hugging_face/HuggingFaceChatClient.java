package com.newwork.human_resources_app.client.hugging_face;

import com.newwork.human_resources_app.client.hugging_face.dto.HFChatRequest;
import com.newwork.human_resources_app.client.hugging_face.dto.HFChatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "huggingFaceChatClient", url = "${huggingface.api.base-url}", configuration = HuggingFaceFeignConfig.class)
public interface HuggingFaceChatClient {

    @PostMapping(value = "/v1/chat/completions")
    HFChatResponse generateChatCompletion(@RequestBody HFChatRequest request);

}