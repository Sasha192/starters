package org.wpstarters.multitenancyspringbootstarter.schema;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SchemaPerTenant;

@Configuration
@EnableJpaRepositories(
        basePackages = {"org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant"},
        entityManagerFactoryRef = "defaultEntityManagerFactory",
        transactionManagerRef = "defaultTransactionManager"
)
@Conditional(SchemaPerTenant.class)
public class DefaultSchemaJpaRepositoriesConfig {
}
