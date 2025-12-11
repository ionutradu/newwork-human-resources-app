package com.newwork.human_resources_app.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestcontainersConfig {

    private static final DockerImageName MONGO_IMAGE = DockerImageName.parse("mongo:8.0");

    @Bean
    public MongoDBContainer mongoDBContainer() {
        var container = new MongoDBContainer(MONGO_IMAGE).withExposedPorts(27017).withReuse(true);

        container.start();
        return container;
    }
}
