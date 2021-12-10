package org.wpstarters.multitenancyspringbootstarter.schema;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.wpstarters.multitenancyspringbootstarter.SchemaPerTenantAutoConfiguration;
import org.wpstarters.multitenancyspringbootstarter.YamlEnvironmentBeanProcessor;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SchemaPerTenant;

@Configuration
@Import(value = {
        SchemaPerTenantAutoConfiguration.class,
        YamlEnvironmentBeanProcessor.class,
        DefaultSchemaJpaRepositoriesConfig.class,
        TenancyJpaRepositoriesConfig.class
})
@Conditional(SchemaPerTenant.class)
public class SchemaTestConfiguration {
}
