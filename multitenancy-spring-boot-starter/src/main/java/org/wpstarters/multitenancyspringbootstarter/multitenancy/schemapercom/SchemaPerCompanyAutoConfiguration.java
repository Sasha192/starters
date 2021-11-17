package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SchemaPerTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.domain.SimpleTenantRepository;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders.IMigrationPathProvider;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders.IMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders.MigrationPathsProvider;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders.MigrationsService;

import javax.sql.DataSource;

@Configuration
@Conditional(SchemaPerTenant.class)
@Import(value = {
        DefaultPersistenceConfig.class,
        TenancyPersistenceConfig.class
})
public class SchemaPerCompanyAutoConfiguration {


    @Bean(value = "tenantIdentifierResolver")
    public CurrentTenantIdentifierResolver tenantIdentifierResolver() {
        return new CustomCurrentTenantIdentifierResolver();
    }

    @Bean("tenantConnectionProvider")
    public MultiTenantConnectionProvider tenantConnectionProvider(DataSource datasource,
                                                                  SimpleTenantRepository tenantRepository,
                                                                  StarterConfigurationProperties properties) {
        return new CustomMultiTenantConnectionProvider(datasource, tenantRepository, properties.getCache());
    }

    @Bean("multitenancyLiquibaseProperties")
    @ConfigurationProperties(prefix = "wp37-multitenancy-starter.multitenancy-liquibase")
    @ConditionalOnProperty(name = "wp37-multitenancy-starter.multitenancy-liquibase.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public CustomLiquibaseProperties multitenancyLiquibaseProperties() {
        return new CustomLiquibaseProperties();
    }


    @Bean("defaultLiquibaseProperties")
    @ConfigurationProperties(prefix = "wp37-multitenancy-starter.default-liquibase")
    @ConditionalOnProperty(name = "wp37-multitenancy-starter.default-liquibase.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public CustomLiquibaseProperties defaultLiquibaseProperties() {
        return new CustomLiquibaseProperties();
    }

    @Bean
    @ConditionalOnProperty(name = {
            "wp37-multitenancy-starter.default-liquibase.enabled"},
            havingValue = "true",
            matchIfMissing = true)
    public IMigrationPathProvider migrationPathProvider() {
        return new MigrationPathsProvider(new PathMatchingResourcePatternResolver());
    }

    @Bean("multiTenantSpringLiquibase")
    @ConditionalOnProperty(name = "wp37-multitenancy-starter.default-liquibase.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public CustomMultitenantSpringLiquibaseOnStartup multiTenantSpringLiquibase(SimpleTenantRepository tenantRepository,
                                                                                IMigrationsService migrationsService,
                                                                                StarterConfigurationProperties starterProperties,
                                                                                @Value("${wp37-multitenancy-starter.tenantScan.enabled}") boolean liquibaseForTenants) {
        return new CustomMultitenantSpringLiquibaseOnStartup(
                tenantRepository,
                migrationsService,
                starterProperties,
                liquibaseForTenants
        );
    }

    @Bean
    @ConditionalOnProperty(name = "wp37-multitenancy-starter.default-liquibase.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public IMigrationsService migrationsService(DataSource dataSource,
                                                JdbcTemplate jdbcTemplate,
                                                @Qualifier("multitenancyLiquibaseProperties")
                                                @Autowired(required = false) LiquibaseProperties tenantProperties,
                                                IMigrationPathProvider migrationPathProvider,
                                                @Qualifier("defaultLiquibaseProperties") LiquibaseProperties defaultProperties,
                                                StarterConfigurationProperties starterProperties) {
        return new MigrationsService(jdbcTemplate, tenantProperties, defaultProperties, starterProperties, migrationPathProvider, dataSource);
    }

}
