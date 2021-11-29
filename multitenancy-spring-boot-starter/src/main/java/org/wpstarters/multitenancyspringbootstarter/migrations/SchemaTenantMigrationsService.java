package org.wpstarters.multitenancyspringbootstarter.migrations;

import liquibase.exception.LiquibaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenant;

import javax.sql.DataSource;
import java.net.URI;
import java.util.List;

public class SchemaTenantMigrationsService extends BaseMigrationsService<SchemaTenant> {

    private static final Logger logger = LoggerFactory.getLogger(SchemaTenantMigrationsService.class);
    private static final String CREATE_SCHEMA = "CREATE SCHEMA %s";
    private static final String DROP_SCHEMA_IF_EXIST = "DROP SCHEMA IF EXISTS %s";

    public SchemaTenantMigrationsService(JdbcTemplate jdbcTemplate,
                                         LiquibaseProperties tenantProperties,
                                         LiquibaseProperties defaultProperties,
                                         StarterConfigurationProperties starterProperties,
                                         IMigrationPathProvider migrationPathProvider,
                                         DataSource dataSource) {
        super(jdbcTemplate, tenantProperties, defaultProperties, starterProperties, migrationPathProvider, dataSource);
    }

    @Override
    public void createSchema(String schema) throws DataAccessException {
        String createSchemaQuery = String.format(CREATE_SCHEMA, schema);
        logger.debug("NEW SCHEMA CREATING {}", createSchemaQuery);
        jdbcTemplate.execute(createSchemaQuery);
    }

    @Override
    public void deleteSchema(String schema) {
        String dropSchemaQuery = String.format(DROP_SCHEMA_IF_EXIST, schema);
        logger.debug("SCHEMA DROPPING {}", dropSchemaQuery);
        jdbcTemplate.execute(dropSchemaQuery);
    }

    @Override
    public void runMigrationsOnTenant(SchemaTenant tenant) throws LiquibaseException {
        List<URI> tenantsMigrationsPaths = migrationPathProvider.tenantsMigrationsPaths();
        runMigrations(tenant, tenantProperties, tenantsMigrationsPaths);
    }

}
