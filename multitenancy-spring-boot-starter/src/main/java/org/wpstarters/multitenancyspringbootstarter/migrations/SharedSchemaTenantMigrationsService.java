package org.wpstarters.multitenancyspringbootstarter.migrations;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenant;

import javax.sql.DataSource;

public class SharedSchemaTenantMigrationsService extends BaseMigrationsService<SharedSchemaTenant> {

    public SharedSchemaTenantMigrationsService(JdbcTemplate jdbcTemplate,
                                               LiquibaseProperties tenantProperties,
                                               LiquibaseProperties defaultProperties,
                                               StarterConfigurationProperties starterProperties,
                                               IMigrationPathProvider migrationPathProvider,
                                               DataSource dataSource) {
        super(jdbcTemplate, tenantProperties, defaultProperties, starterProperties, migrationPathProvider, dataSource);
    }

    @Override
    public void createSchema(String schema) {
        throw new UnsupportedOperationException("createSchema is unsupported");
    }

    @Override
    public void deleteSchema(String schema) {
        throw new UnsupportedOperationException("deleteSchema is unsupported");
    }

    @Override
    public void runMigrationsOnTenant(SharedSchemaTenant tenant) {
        throw new UnsupportedOperationException("runMigrationsOnTenant is unsupported");
    }

}
