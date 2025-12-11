package com.newwork.human_resources_app.core.client.hugging_face.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HFChoice(
        Integer index,
        HFMessage message,
        @JsonProperty(value = "finish_reason", isRequired = OptBoolean.FALSE)
                String finishReason) {}
