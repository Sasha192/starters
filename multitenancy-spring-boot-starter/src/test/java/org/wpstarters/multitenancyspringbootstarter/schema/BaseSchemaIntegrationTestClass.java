package org.wpstarters.multitenancyspringbootstarter.schema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenantManagementService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenantReadRepository;

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
	
	@Autowired
	private SchemaTenantManagementService tenantManagementService;
	
	@Autowired
	private SchemaTenantReadRepository tenantReadRepository;

	@BeforeEach
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.alwaysDo(MockMvcResultHandlers.print())
				.build();

		init();
	}

	public abstract void init() throws Exception;
	
	@AfterEach
	public void cleanup() {
		
		beforeCleanup();

		Iterable<SchemaTenant> schemaTenants = tenantReadRepository.findAll();
		
		
		for (SchemaTenant tenant: schemaTenants) {
			
			tenantManagementService.removeTenant(tenant);
			
		}
		
		afterCleanup();
		
	}

	protected abstract void afterCleanup();

	protected abstract void beforeCleanup();

}
