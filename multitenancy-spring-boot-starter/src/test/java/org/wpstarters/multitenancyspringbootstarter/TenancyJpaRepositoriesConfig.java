package org.wpstarters.multitenancyspringbootstarter;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {"org.wpstarters.multitenancyspringbootstarter.domain"},
        entityManagerFactoryRef = "tenancyEntityManagerFactory",
        transactionManagerRef = "tenancyTransactionManager"
)
public class TenancyJpaRepositoriesConfig {
}
