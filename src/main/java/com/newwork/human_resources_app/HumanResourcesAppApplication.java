package com.newwork.human_resources_app;

import com.newwork.human_resources_app.config.HuggingFaceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(
		exclude = {
				DataSourceAutoConfiguration.class,
				HibernateJpaAutoConfiguration.class,
		}
)
@EnableFeignClients
@EnableConfigurationProperties(HuggingFaceProperties.class)
public class HumanResourcesAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(HumanResourcesAppApplication.class, args);
	}

}
