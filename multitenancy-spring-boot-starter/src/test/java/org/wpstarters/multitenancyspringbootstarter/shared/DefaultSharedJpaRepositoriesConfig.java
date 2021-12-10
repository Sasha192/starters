package org.wpstarters.multitenancyspringbootstarter.shared;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SharedSchema;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema.BasicTenantAwareEntity;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared",
                "org.wpstarters.multitenancyspringbootstarter.shared.domain"},
        entityManagerFactoryRef = "defaultEntityManagerFactory",
        transactionManagerRef = "defaultTransactionManager"/*,
        includeFilters = {
                @ComponentScan.Filter(
                        type = ASSIGNABLE_TYPE,
                        classes = {
                                BasicTenantAwareEntity.class
                        }
                )
        }*/
)
@Conditional(SharedSchema.class)
public class DefaultSharedJpaRepositoriesConfig {
}
