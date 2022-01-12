package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wpstarters.commonwebstarter.ITenantIDResolver;
import org.wpstarters.multitenancyspringbootstarter.migrations.IMigrationPathProvider;
import org.wpstarters.multitenancyspringbootstarter.migrations.MigrationPathsProvider;
import org.wpstarters.multitenancyspringbootstarter.migrations.NoneSchemaMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.NoneTenants;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.NoneSchemaTenant;

import javax.sql.DataSource;

@Configuration
@Conditional(NoneTenants.class)
@Import(value = {
        DefaultNoneSchemaPersistenceConfig.class
})
public class NoneSchemaAutoConfiguration {

    @Bean
    public ITenantIDResolver<NoneSchemaTenant> sharedTenantIdResolver() {
        return tenantId -> tenantId;
    }

    @Bean
    public IMigrationPathProvider migrationPathProvider() {
        return new MigrationPathsProvider(new PathMatchingResourcePatternResolver());
    }

    @Bean
    public NoneSchemaMigrationsService sharedSchemaTenantMigrationsService(DataSource dataSource,
                                                                           JdbcTemplate jdbcTemplate,
                                                                           @Qualifier("multitenancyLiquibaseProperties")
                                                                                       @Autowired(required = false) LiquibaseProperties tenantProperties,
                                                                           IMigrationPathProvider migrationPathProvider,
                                                                           @Qualifier("defaultLiquibaseProperties") LiquibaseProperties defaultProperties,
                                                                           StarterConfigurationProperties starterProperties) {
      return new NoneSchemaMigrationsService(jdbcTemplate, tenantProperties, defaultProperties, starterProperties, migrationPathProvider, dataSource);
    }


    @Bean
    public SpringLiquibaseOnStartup sharedSchemaSpringLiquibaseOnStartup(NoneSchemaMigrationsService migrationsService) {
        return new SpringLiquibaseOnStartup(migrationsService);
    }

}
