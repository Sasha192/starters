package org.wpstarters.multitenancyspringbootstarter.schema.tests;

import org.assertj.core.api.Assertions;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wpstarters.multitenancyspringbootstarter.schema.BaseSchemaIntegrationTestClass;
import org.wpstarters.multitenancyspringbootstarter.migrations.SchemaTenantMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenantReadRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class SchemaCreationTest extends BaseSchemaIntegrationTestClass {

    @Autowired
    SchemaTenantReadRepository tenantReadRepository;

    @Autowired
    SchemaTenantMigrationsService schemaTenantMigrationsService;

    @Autowired
    ITenantManagementService<UUID, SchemaTenant> tenantManagementService;

    @Autowired
    MultiTenantConnectionProvider provider;

    @Override
    public void init() {
        //
    }

    @Test
    public void testTenantCreation() {

        String checkSchema = " SELECT table_name FROM information_schema.tables WHERE table_schema = '%s' ";

        SchemaTenant newTenant = (SchemaTenant) tenantManagementService.createTenant();
        try (Connection con = provider.getAnyConnection()) {

            try (ResultSet rs = con.createStatement()
                    .executeQuery(String.format(checkSchema, newTenant.getSchema()))) {

                boolean tableExist = false;
                while (rs.next()) {
                    String tableName = rs.getString("table_name");
                    tableExist = tableName.equals("tenant_test_table");
                }

                Assertions.assertThat(tableExist).isTrue();
            }

            schemaTenantMigrationsService.deleteSchema(newTenant.getSchema());

        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }

    @Override
    protected void afterCleanup() {
        // do nothing
    }

    @Override
    protected void beforeCleanup() {
        // do nothing
    }
}
