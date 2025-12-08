package com.newwork.human_resources_app;

import org.springframework.ai.vectorstore.weaviate.autoconfigure.WeaviateVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(
		exclude = {
				DataSourceAutoConfiguration.class,
				HibernateJpaAutoConfiguration.class,
				WeaviateVectorStoreAutoConfiguration.class
		}
)
public class HumanResourcesAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(HumanResourcesAppApplication.class, args);
	}

}
