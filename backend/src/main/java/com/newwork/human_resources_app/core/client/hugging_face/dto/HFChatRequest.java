package com.newwork.human_resources_app.core.client.hugging_face.dto;

import java.util.List;

public record HFChatRequest(String model, List<HFMessage> messages) {
    public HFChatRequest(String model, List<HFMessage> messages) {
        this.model = model;
        this.messages = messages;
    }
}
