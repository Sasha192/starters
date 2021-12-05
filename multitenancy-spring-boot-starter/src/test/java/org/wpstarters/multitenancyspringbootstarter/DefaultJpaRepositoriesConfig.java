package org.wpstarters.multitenancyspringbootstarter;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {"org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud"},
        entityManagerFactoryRef = "defaultEntityManagerFactory",
        transactionManagerRef = "defaultTransactionManager"
)
public class DefaultJpaRepositoriesConfig {
}
