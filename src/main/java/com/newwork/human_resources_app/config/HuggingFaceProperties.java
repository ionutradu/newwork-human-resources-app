package com.newwork.human_resources_app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "huggingface")
public class HuggingFaceProperties {

    private String model;

}