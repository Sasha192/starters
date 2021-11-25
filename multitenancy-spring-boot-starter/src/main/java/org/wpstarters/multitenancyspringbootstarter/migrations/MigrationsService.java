package org.wpstarters.multitenancyspringbootstarter.migrations;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wpstarters.multitenancyspringbootstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.domain.SimpleTenant;

import javax.sql.DataSource;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import static org.wpstarters.multitenancyspringbootstarter.multitenancy.SpringLiquibaseBuilder.buildDefault;

public class MigrationsService implements IMigrationsService, ResourceLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(MigrationsService.class);
    private static final String CREATE_SCHEMA = "CREATE SCHEMA %s";
    private static final String DROP_SCHEMA_IF_EXIST = "DROP SCHEMA IF EXISTS %s";

    private final JdbcTemplate jdbcTemplate;
    private final LiquibaseProperties tenantProperties;
    private final LiquibaseProperties defaultProperties;
    private final StarterConfigurationProperties starterProperties;
    private final IMigrationPathProvider migrationPathProvider;
    private final DataSource dataSource;
    private ResourceLoader resourceLoader;

    public MigrationsService(JdbcTemplate jdbcTemplate,
                             LiquibaseProperties tenantProperties,
                             LiquibaseProperties defaultProperties,
                             StarterConfigurationProperties starterProperties,
                             IMigrationPathProvider migrationPathProvider,
                             DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.tenantProperties = tenantProperties;
        this.defaultProperties = defaultProperties;
        this.starterProperties = starterProperties;
        this.migrationPathProvider = migrationPathProvider;
        this.dataSource = dataSource;
    }

    @Override
    public void createSchema(String schema) throws DataAccessException {
        String createSchemaQuery = String.format(CREATE_SCHEMA, schema);
        jdbcTemplate.execute(createSchemaQuery);
    }

    @Override
    public void deleteSchema(String schema) {
        String dropSchemaQuery = String.format(DROP_SCHEMA_IF_EXIST, schema);
        jdbcTemplate.execute(dropSchemaQuery);
    }

    @Override
    public void runMigrationsOnTenant(Tenant<?> tenant) throws LiquibaseException {
        List<URI> tenantsMigrationsPaths = migrationPathProvider.tenantsMigrationsPaths();
        runMigrations(tenant, tenantProperties, tenantsMigrationsPaths);
    }

    @Override
    public void runMigrationsOnDefaultTenant()
            throws LiquibaseException {
        Tenant<?> defaultTenant = new SimpleTenant.Builder()
                .schema(starterProperties.getDefaultSchema())
                .active(true)
                .build();
        List<URI> defaultSchemaMigrationsUris = migrationPathProvider.defaultSchemaMigrationsPaths();
        runMigrations(defaultTenant, defaultProperties, defaultSchemaMigrationsUris);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private void runMigrations(Tenant<?> tenant,
                               LiquibaseProperties liquibaseProperties,
                               List<URI> migrationsUris) throws LiquibaseException {
        for (URI migrationUri: migrationsUris) {
            String migrationPath = migrationPath(migrationUri);
            SpringLiquibase liquibase = buildDefault(dataSource, tenant.getSchema(), migrationPath, resourceLoader, liquibaseProperties);
            liquibase.afterPropertiesSet();
        }
    }


    ///
    ///     MIGRATIONS' PATHS ISSUES (NOT MY CODE):
    ///


    private String migrationPath(URI migrationsUri) {
        String migrationPath = migrationsUri.getPath();
        // We could search migrations with the standard SpringResourceAccessor using unique migration paths like classpath*:/unique-starter-name/starter_migrations.xml,
        // but it is dramatically slow, since it searches the migration and all included migrations in all libs.
        // So having absolute path on our hands we narrow down the search scope by using FileSystemResourceAccessor with specified rootPath.
        // To support also relative classpath URIs we rely on the SpringResourceAccessor's behavior
        if (!isClasspathResource(migrationsUri)) {
            logger.debug("FileSystemResourceAccessor is to find resources in absolute path: {}", migrationPath);
            Path absolutePath = normalizeAbsolutePath(migrationsUri);
            // Liquibase migration file must be in exactly directory long from jar root, e.g. any-starter.jar/<migrations_dir>/starter-migrations.xml
            // For non-jar path it works as well
            Path rootPath = absolutePath.getParent().getParent();
            // Get migration path relative to jar root, e.g. starter-db-liquibase/starter-migrations.xml
            migrationPath = rootPath.relativize(absolutePath).toString();
            // Liquibase will know where to look for the migration (rootPath), so the relative migrationPath will be found and registered as filename field in databasechangelog table
        }
        return migrationPath;
    }

    private boolean isClasspathResource(URI migrationUri) {
        return "classpath".equals(migrationUri.getScheme());
    }

    private static Path normalizeAbsolutePath(URI uri) {
        // jar URIs getPath() may be null
        String path = uri.getPath() != null ? uri.getPath() : uri.toString();
        // jar:file:/libs/starter.jar!/liquibase/migration.xml -> /libs/starter.jar/liquibase/migration.xml
        String migrationPath = path.replaceFirst("jar:file:", "").replaceFirst("jar!", "jar");
        // Fix absolute path for Windows
        migrationPath = migrationPath.replaceFirst("^/([A-Z]:/)", "$1");
        return Path.of(migrationPath);
    }
}
