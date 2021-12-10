package org.wpstarters.multitenancyspringbootstarter.shared;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.wpstarters.multitenancyspringbootstarter.MultitenancyStarterConfiguration;
import org.wpstarters.multitenancyspringbootstarter.YamlEnvironmentBeanProcessor;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SharedSchema;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema.SharedSchemaAutoConfiguration;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenantReadRepository;

@Configuration
@Import(value = {
        MultitenancyStarterConfiguration.class,
        SharedSchemaAutoConfiguration.class,
        DefaultSharedJpaRepositoriesConfig.class,
        SharedSchemaTenantReadRepository.class,
        YamlEnvironmentBeanProcessor.class
})
@Conditional(SharedSchema.class)
public class SharedSchemaTests {
}
