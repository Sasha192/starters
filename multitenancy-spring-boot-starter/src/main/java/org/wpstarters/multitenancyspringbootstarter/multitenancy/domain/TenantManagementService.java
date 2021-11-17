package org.wpstarters.multitenancyspringbootstarter.multitenancy.domain;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;
import org.wpstarters.multitenancyspringbootstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.exceptions.TenantCreationException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.UUID;

import static org.wpstarters.multitenancyspringbootstarter.multitenancy.SpringLiquibaseBuilder.buildDefault;

@Service
public class TenantManagementService implements ITenantManagementService<UUID> {

    private static final Logger logger = LoggerFactory.getLogger(TenantManagementService.class);

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final LiquibaseProperties liquibaseProperties;
    private final ResourceLoader resourceLoader;
    private final SimpleTenantRepository tenantRepository;

    public TenantManagementService(DataSource dataSource,
                                       JdbcTemplate jdbcTemplate,
                                       @Qualifier("multitenancyLiquibaseProperties") LiquibaseProperties liquibaseProperties,
                                       ResourceLoader resourceLoader,
                                       SimpleTenantRepository tenantRepository) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.liquibaseProperties = liquibaseProperties;
        this.resourceLoader = resourceLoader;
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Tenant<UUID> createTenant() {

        SimpleTenant tenant = new SimpleTenant.Builder()
                .id(UUID.randomUUID())
                .active(true)
                .build();
        tenant.setSchema(
                UUID.randomUUID().toString().replace("-","")
        );

        try {
            createSchema(tenant.getSchema());
            runLiquibase(dataSource, tenant.getSchema());
        } catch (DataAccessException e) {
            throw new TenantCreationException("Error when creating schema: " + tenant.getSchema(), e);
        } catch (LiquibaseException e) {
            throw new TenantCreationException("Error when populating schema: ", e);
        }
        tenantRepository.save(tenant);

        return tenant;
    }

    private void createSchema(String schema) {
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("CREATE SCHEMA " + schema));
    }

    private void runLiquibase(DataSource dataSource, String schema) throws LiquibaseException {
        //SpringLiquibase liquibase = buildDefault(dataSource, schema, resourceLoader, liquibaseProperties);
        //liquibase.afterPropertiesSet();
    }

}
