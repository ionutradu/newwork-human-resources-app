package com.newwork.human_resources_app.client.hugging_face.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HFMessage(
    String role,
    String content
) {}