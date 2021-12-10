package org.wpstarters.multitenancyspringbootstarter.schema.tests;

import org.assertj.core.api.Assertions;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wpstarters.commonwebstarter.TenantContext;
import org.wpstarters.multitenancyspringbootstarter.schema.BaseSchemaIntegrationTestClass;
import org.wpstarters.multitenancyspringbootstarter.schema.domain.TenantTestEntity;
import org.wpstarters.multitenancyspringbootstarter.schema.domain.TenantSchemaTestEntityRepository;
import org.wpstarters.multitenancyspringbootstarter.migrations.SchemaTenantMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantManagementService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenantReadRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.UUID;


public class EntityTest extends BaseSchemaIntegrationTestClass {

    @Autowired
    SchemaTenantReadRepository tenantReadRepository;

    @Autowired
    SchemaTenantMigrationsService schemaTenantMigrationsService;

    @Autowired
    ITenantManagementService<UUID, SchemaTenant> tenantManagementService;

    @Autowired
    MultiTenantConnectionProvider provider;

    @Autowired
    TenantSchemaTestEntityRepository testEntityRepository;

    ThreadLocal<SchemaTenant> schemaTenantThreadLocal = new ThreadLocal<>();

    @BeforeEach
    public void init() throws Exception {

        String checkSchema = " SELECT table_name FROM information_schema.tables WHERE table_schema = '%s' ";

        SchemaTenant newTenant = (SchemaTenant) tenantManagementService.createTenant();
        schemaTenantThreadLocal.set(newTenant);

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
        }
    }

    @Test
    public void testCreateUpdateDeleteRead() {

        TenantTestEntity testEntity = new TenantTestEntity.Builder()
                .id(UUID.randomUUID())
                .name("Name")
                .build();

        TenantContext.setTenantContext(schemaTenantThreadLocal.get().getId().toString());
        testEntity = testEntityRepository.save(testEntity);
        Assertions.assertThat(testEntityRepository.findById(testEntity.getId()).get().getName()).isEqualTo("Name");
        testEntity.setName("Another name");
        testEntityRepository.save(testEntity);
        testEntityRepository.deleteById(testEntity.getId());


        Assertions.assertThat(testEntityRepository.findById(testEntity.getId()).isEmpty()).isTrue();

    }

    @AfterEach
    public void after() {
        schemaTenantMigrationsService.deleteSchema(schemaTenantThreadLocal.get().getSchema());
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
