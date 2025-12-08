package com.newwork.human_resources_app;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.weaviate.WeaviateContainer;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	MongoDBContainer mongoDbContainer() {
		return new MongoDBContainer(DockerImageName.parse("mongo:latest"));
	}

	@Bean
	@ServiceConnection
	WeaviateContainer weaviateContainer() {
		return new WeaviateContainer(DockerImageName.parse("semitechnologies/weaviate:latest"));
	}

}
