package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenantReadRepository;
import org.wpstarters.multitenancyspringbootstarter.migrations.IMigrationsService;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class CustomMultitenantSpringLiquibaseOnStartup implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(CustomMultitenantSpringLiquibaseOnStartup.class);

    private final SchemaTenantReadRepository schemaTenantRepository;
    private final boolean runLiquibaseForTenants;
    private final IMigrationsService<SchemaTenant> migrationsService;
    private final String defaultSchema;

    public CustomMultitenantSpringLiquibaseOnStartup(SchemaTenantReadRepository tenantRepository,
                                                     IMigrationsService<SchemaTenant> migrationsService,
                                                     StarterConfigurationProperties starterProperties,
                                                     boolean runLiquibaseForTenants) {
        this.schemaTenantRepository = tenantRepository;
        this.migrationsService = migrationsService;
        this.defaultSchema = starterProperties.getDefaultSchema();
        this.runLiquibaseForTenants = runLiquibaseForTenants;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Schema based multitenancy enabled");
        migrationsService.runMigrationsOnDefaultTenant();
        if (runLiquibaseForTenants) {
            List<SchemaTenant> tenants = getAllTenantsExceptDefaultSchema(defaultSchema);
            runOnTenants(tenants);
        }
    }




    private List<SchemaTenant> getAllTenantsExceptDefaultSchema(@NotNull String defaultSchema) {
        Iterable<SchemaTenant> tenants = schemaTenantRepository.findAll();

        return StreamSupport.stream(tenants.spliterator(), false)
                .filter(tenant -> !defaultSchema.equals(tenant.getSchema()))
                .collect(Collectors.toList());
    }

    private void runOnTenants(Iterable<SchemaTenant> tenants)
            throws Exception {
        for(SchemaTenant tenant : tenants) {
            if (tenant.isActive()) {
                logger.info("Initializing Liquibase for tenant " + tenant.getId());
                migrationsService.runMigrationsOnTenant(tenant);
                logger.info("Liquibase ran for tenant " + tenant.getId());
            }
        }
    }

}
