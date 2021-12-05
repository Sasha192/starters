package org.wpstarters.multitenancyspringbootstarter;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.SchemaPerCompanyAutoConfiguration;

@Configuration
@Import(value = {
        MultitenancyStarterConfiguration.class,
        SchemaPerCompanyAutoConfiguration.class,
        YamlEnvironmentBeanProcessor.class,
        DefaultJpaRepositoriesConfig.class,
        TenancyJpaRepositoriesConfig.class
})
public class SchemaTestConfiguration {
}
