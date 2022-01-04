package org.wpstarters.multitenancyspringbootstarter.migrations;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wpstarters.commonwebstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.NoneSchemaTenant;

import javax.sql.DataSource;

public class NoneSchemaMigrationsService extends BaseMigrationsService<NoneSchemaTenant> {

    public NoneSchemaMigrationsService(JdbcTemplate jdbcTemplate,
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
    public void runMigrationsOnTenant(NoneSchemaTenant tenant) {
        throw new UnsupportedOperationException("runMigrationsOnTenant is unsupported");
    }

    @Override
    protected Tenant<?> getDefaultTenant() {
        return new NoneSchemaTenant.Builder()
                .active(true)
                .schema(starterProperties.getDefaultSchema())
                .build();
    }
}
