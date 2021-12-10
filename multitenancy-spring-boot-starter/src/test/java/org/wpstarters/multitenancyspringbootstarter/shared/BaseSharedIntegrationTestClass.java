package org.wpstarters.multitenancyspringbootstarter.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Conditional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SharedSchema;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenantManagementService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenantReadRepository;

@SpringJUnitWebConfig
@SpringBootTest(classes = {SharedSchemaTests.class, DefaultSharedJpaRepositoriesConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test_shared")
@TestPropertySource(locations = "classpath:/application-test_shared.yaml")
@Conditional(SharedSchema.class)
public abstract class BaseSharedIntegrationTestClass {

	protected MockMvc mockMvc;


	protected ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	protected WebApplicationContext webApplicationContext;
	
	@Autowired
	private SharedSchemaTenantManagementService tenantManagementService;
	
	@Autowired
	private SharedSchemaTenantReadRepository tenantReadRepository;

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

		Iterable<SharedSchemaTenant> sharedTenants = tenantReadRepository.findAll();
		
		
		for (SharedSchemaTenant tenant: sharedTenants) {
			
			tenantManagementService.removeTenant(tenant);
			
		}
		
		afterCleanup();
		
	}

	protected abstract void afterCleanup();

	protected abstract void beforeCleanup();

}
