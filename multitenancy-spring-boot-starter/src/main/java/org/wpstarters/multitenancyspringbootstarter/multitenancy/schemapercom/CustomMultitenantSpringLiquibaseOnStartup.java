package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.wpstarters.multitenancyspringbootstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.domain.SimpleTenantRepository;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders.IMigrationsService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class CustomMultitenantSpringLiquibaseOnStartup implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(CustomMultitenantSpringLiquibaseOnStartup.class);

    private final SimpleTenantRepository tenantRepository;
    private final boolean liquibaseForTenants;
    private final IMigrationsService migrationsService;
    private final String defaultSchema;

    public CustomMultitenantSpringLiquibaseOnStartup(SimpleTenantRepository tenantRepository,
                                                     IMigrationsService migrationsService,
                                                     StarterConfigurationProperties starterProperties,
                                                     boolean liquibaseForTenants) {
        this.tenantRepository = tenantRepository;
        this.migrationsService = migrationsService;
        this.defaultSchema = starterProperties.getDefaultSchema();
        this.liquibaseForTenants = liquibaseForTenants;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Schema based multitenancy enabled");
        migrationsService.runMigrationsOnDefaultTenant();
        if (liquibaseForTenants) {
            List<? extends Tenant<?>> tenants = getAllTenantsExceptDefaultSchema(defaultSchema);
            runOnTenants(tenants);
        }
    }


    private List<? extends Tenant<?>> getAllTenantsExceptDefaultSchema(@NotNull String defaultSchema) {
        Iterable<? extends Tenant<?>> tenants = tenantRepository.findAll();

        return StreamSupport.stream(tenants.spliterator(), false)
                .filter(tenant -> !defaultSchema.equals(tenant.getSchema()))
                .collect(Collectors.toList());
    }

    private void runOnTenants(Iterable<? extends Tenant<?>> tenants)
            throws Exception {
        for(Tenant<?> tenant : tenants) {
            if (tenant.isActive()) {
                logger.info("Initializing Liquibase for tenant " + tenant.getId());
                migrationsService.runMigrationsOnTenant(tenant);
                logger.info("Liquibase ran for tenant " + tenant.getId());
            }
        }
    }

}
