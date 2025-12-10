package com.newwork.human_resources_app.client.hugging_face.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HFChatResponse(
    String id,
    String object,
    Long created,
    String model,
    List<HFChoice> choices
) {}

