package com.newwork.human_resources_app.client.hugging_face;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HuggingFaceFeignConfig {

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor huggingFaceAuthInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + apiKey);
            requestTemplate.header("Content-Type", "application/json");
        };
    }
}