package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

import org.springframework.beans.factory.InitializingBean;
import org.wpstarters.multitenancyspringbootstarter.migrations.IMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenant;


public class DefaultSharedSchemaSpringLiquibaseOnStartup
        implements InitializingBean {

    private final IMigrationsService<SharedSchemaTenant> migrationsService;

    public DefaultSharedSchemaSpringLiquibaseOnStartup(IMigrationsService<SharedSchemaTenant> migrationsService) {
        this.migrationsService = migrationsService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        migrationsService.runMigrationsOnDefaultTenant();
    }

}
