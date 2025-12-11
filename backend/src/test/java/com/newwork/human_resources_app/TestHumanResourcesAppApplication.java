package com.newwork.human_resources_app;

import org.springframework.boot.SpringApplication;

public class TestHumanResourcesAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(HumanResourcesAppApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
