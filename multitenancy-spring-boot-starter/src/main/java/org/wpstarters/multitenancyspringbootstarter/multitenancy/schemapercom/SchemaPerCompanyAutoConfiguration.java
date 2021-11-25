package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import org.wpstarters.multitenancyspringbootstarter.migrations.IMigrationPathProvider;
import org.wpstarters.multitenancyspringbootstarter.migrations.IMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.migrations.MigrationPathsProvider;
import org.wpstarters.multitenancyspringbootstarter.migrations.MigrationsService;

import javax.sql.DataSource;

@Configuration
@Conditional(SchemaPerTenant.class)
@Import(value = {
        DefaultSchemaPerCompanyPersistenceConfig.class,
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
    public CustomLiquibaseProperties multitenancyLiquibaseProperties() {
        return new CustomLiquibaseProperties();
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

    @Bean("multiTenantSpringLiquibase")
    public CustomMultitenantSpringLiquibaseOnStartup multiTenantSpringLiquibase(SimpleTenantRepository tenantRepository,
                                                                                IMigrationsService migrationsService,
                                                                                StarterConfigurationProperties starterProperties,
                                                                                @Value("${wp37-multitenancy-starter.runLiquibaseForTenants}")
                                                                                            boolean runLiquibaseForTenants) {
        return new CustomMultitenantSpringLiquibaseOnStartup(
                tenantRepository,
                migrationsService,
                starterProperties,
                runLiquibaseForTenants
        );
    }

    @Bean
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
