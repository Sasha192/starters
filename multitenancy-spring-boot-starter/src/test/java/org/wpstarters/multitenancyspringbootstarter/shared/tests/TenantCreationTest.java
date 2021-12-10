package org.wpstarters.multitenancyspringbootstarter.shared.tests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.wpstarters.commonwebstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.migrations.SharedSchemaTenantMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantManagementService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenantReadRepository;
import org.wpstarters.multitenancyspringbootstarter.shared.BaseSharedIntegrationTestClass;

import java.util.UUID;


public class TenantCreationTest extends BaseSharedIntegrationTestClass {

    @Autowired
    SharedSchemaTenantReadRepository tenantReadRepository;

    @Autowired
    SharedSchemaTenantMigrationsService schemaTenantMigrationsService;

    @Autowired
    ITenantManagementService<UUID, SharedSchemaTenant> tenantManagementService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void init() {
        //
    }

    @Test
    public void testTenantCreation() {

        Tenant<UUID> tenant = tenantManagementService.createTenant();

        Tenant<UUID> newTenant = jdbcTemplate.queryForObject(
                "SELECT * FROM tenants t WHERE t.id=" + tenant.getId(),
                SharedSchemaTenant.class
        );

        Assertions.assertThat(tenant).isEqualTo(newTenant);

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
