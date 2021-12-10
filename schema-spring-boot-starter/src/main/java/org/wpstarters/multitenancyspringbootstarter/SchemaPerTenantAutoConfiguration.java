package org.wpstarters.multitenancyspringbootstarter;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SchemaPerTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.*;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenantManagementService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenantReadRepository;
import org.wpstarters.multitenancyspringbootstarter.migrations.SchemaTenantMigrationsService;

import javax.sql.DataSource;
import java.util.function.Predicate;

@Configuration
@Conditional(SchemaPerTenant.class)
@Import(value = {
        DefaultSchemaPerCompanyPersistenceConfig.class,
        TenancyPersistenceConfig.class,
        StarterConfigurationProperties.class,
        DataSource.class,
        SchemaTenantReadRepository.class,
})
@SpringBootApplication
public class SchemaPerTenantAutoConfiguration {


    @Bean(value = "tenantIdentifierResolver")
    public CurrentTenantIdentifierResolver tenantIdentifierResolver(StarterConfigurationProperties starterConfigurationProperties,
                                                                    @Autowired(required = false) Predicate<String> enabledTenantCandidate) {
        return new CustomCurrentTenantIdentifierResolver(starterConfigurationProperties, enabledTenantCandidate);
    }

    @Bean("tenantConnectionProvider")
    public MultiTenantConnectionProvider tenantConnectionProvider(DataSource datasource,
                                                                  SchemaTenantReadRepository tenantRepository,
                                                                  StarterConfigurationProperties properties) {
        return new CustomMultiTenantConnectionProvider(datasource, tenantRepository, properties.getCache());
    }

    @Bean("multitenancyLiquibaseProperties")
    @ConfigurationProperties(prefix = "wp37-multitenancy-starter.multitenancy-liquibase")
    public CustomLiquibaseProperties multitenancyLiquibaseProperties() {
        return new CustomLiquibaseProperties();
    }


    @Bean
    public IMigrationPathProvider migrationPathProvider() {
        return new MigrationPathsProvider(new PathMatchingResourcePatternResolver());
    }

    @Bean("multiTenantSpringLiquibase")
    public CustomMultitenantSpringLiquibaseOnStartup multiTenantSpringLiquibase(SchemaTenantReadRepository tenantRepository,
                                                                                SchemaTenantMigrationsService migrationsService,
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
    public SchemaTenantMigrationsService migrationsService(DataSource dataSource,
                                                JdbcTemplate jdbcTemplate,
                                                @Qualifier("multitenancyLiquibaseProperties")
                                                @Autowired(required = false) LiquibaseProperties tenantProperties,
                                                IMigrationPathProvider migrationPathProvider,
                                                @Qualifier("defaultLiquibaseProperties") LiquibaseProperties defaultProperties,
                                                StarterConfigurationProperties starterProperties) {
        return new SchemaTenantMigrationsService(jdbcTemplate, tenantProperties, defaultProperties, starterProperties, migrationPathProvider, dataSource);
    }

    @Bean
    public SchemaTenantManagementService schemaTenantManagementService(SchemaTenantReadRepository tenantRepository,
                                                                       SchemaTenantMigrationsService migrationsService) {
        return new SchemaTenantManagementService(tenantRepository, migrationsService);
    }

    @Bean
    public ITenantIDResolver<SchemaTenant> schemaTenantITenantIDResolver() {
        return schema -> schema;
    }

}
