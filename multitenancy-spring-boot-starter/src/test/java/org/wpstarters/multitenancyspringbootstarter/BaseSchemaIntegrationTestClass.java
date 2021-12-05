package org.wpstarters.multitenancyspringbootstarter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringJUnitWebConfig
@SpringBootTest(classes = {SchemaTestConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test_schema")
@TestPropertySource(locations = "classpath:/application-test_schema.yaml")
public abstract class BaseSchemaIntegrationTestClass {

	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.alwaysDo(MockMvcResultHandlers.print())
				.build();

		init();
	}

	public abstract void init() throws Exception;

}
