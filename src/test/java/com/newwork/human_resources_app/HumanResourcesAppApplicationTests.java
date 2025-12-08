package com.newwork.human_resources_app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class HumanResourcesAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
