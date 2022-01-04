package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

import org.springframework.beans.factory.InitializingBean;
import org.wpstarters.multitenancyspringbootstarter.migrations.IMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.NoneSchemaTenant;

public class SpringLiquibaseOnStartup
        implements InitializingBean {

    private final IMigrationsService<NoneSchemaTenant> migrationsService;

    public SpringLiquibaseOnStartup(IMigrationsService<NoneSchemaTenant> migrationsService) {
        this.migrationsService = migrationsService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        migrationsService.runMigrationsOnDefaultTenant();
    }

}
