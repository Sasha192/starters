package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wpstarters.commonwebstarter.ITenantIDResolver;
import org.wpstarters.multitenancyspringbootstarter.migrations.IMigrationPathProvider;
import org.wpstarters.multitenancyspringbootstarter.migrations.MigrationPathsProvider;
import org.wpstarters.multitenancyspringbootstarter.migrations.SharedSchemaTenantMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SharedSchema;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.CustomLiquibaseProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenantManagementService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenantReadRepository;

import javax.sql.DataSource;

@Configuration
@Conditional(SharedSchema.class)
@Import(value = {
        DefaultSharedSchemaPersistenceConfig.class
})
public class SharedSchemaAutoConfiguration {

    @Bean
    public ITenantIDResolver<SchemaTenant> sharedTenantIdResolver() {
        return tenantId -> tenantId;
    }

    @Bean("defaultLiquibaseProperties")
    @ConfigurationProperties(prefix = "wp37-multitenancy-starter.default-liquibase")
    public CustomLiquibaseProperties defaultLiquibaseProperties() {
        return new CustomLiquibaseProperties();
    }

    @Bean
    public IMigrationPathProvider migrationPathProvider() {
        return new MigrationPathsProvider(new PathMatchingResourcePatternResolver());
    }

    @Bean
    public SharedSchemaTenantManagementService tenantManagementService(SharedSchemaTenantReadRepository tenantRepository) {
        return new SharedSchemaTenantManagementService(tenantRepository);
    }

    @Bean
    public SharedSchemaTenantMigrationsService sharedSchemaTenantMigrationsService(DataSource dataSource,
                                                                                   JdbcTemplate jdbcTemplate,
                                                                                   @Qualifier("multitenancyLiquibaseProperties")
                                                                                       @Autowired(required = false) LiquibaseProperties tenantProperties,
                                                                                   IMigrationPathProvider migrationPathProvider,
                                                                                   @Qualifier("defaultLiquibaseProperties") LiquibaseProperties defaultProperties,
                                                                                   StarterConfigurationProperties starterProperties) {
      return new SharedSchemaTenantMigrationsService(jdbcTemplate, tenantProperties, defaultProperties, starterProperties, migrationPathProvider, dataSource);
    }


    @Bean
    public DefaultSharedSchemaSpringLiquibaseOnStartup sharedSchemaSpringLiquibaseOnStartup(SharedSchemaTenantMigrationsService migrationsService) {
        return new DefaultSharedSchemaSpringLiquibaseOnStartup(migrationsService);
    }

}
