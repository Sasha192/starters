package org.wpstarters.multitenancyspringbootstarter.shared.tests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.wpstarters.commonwebstarter.TenantContext;
import org.wpstarters.multitenancyspringbootstarter.migrations.SharedSchemaTenantMigrationsService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantManagementService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.SharedSchemaTenantReadRepository;
import org.wpstarters.multitenancyspringbootstarter.shared.BaseSharedIntegrationTestClass;
import org.wpstarters.multitenancyspringbootstarter.shared.domain.TenantTestEntity;
import org.wpstarters.multitenancyspringbootstarter.shared.domain.TenantSharedTestEntityRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EntityTest extends BaseSharedIntegrationTestClass {

    @Autowired
    SharedSchemaTenantReadRepository tenantReadRepository;

    @Autowired
    SharedSchemaTenantMigrationsService schemaTenantMigrationsService;

    @Autowired
    ITenantManagementService<UUID, SharedSchemaTenant> tenantManagementService;

    @Autowired
    TenantSharedTestEntityRepository testEntityRepository;

    SharedSchemaTenant tenant1;
    SharedSchemaTenant tenant2;

    @BeforeEach
    public void init() {

        tenant1 = (SharedSchemaTenant) tenantManagementService.createTenant();
        tenant2 = (SharedSchemaTenant) tenantManagementService.createTenant();

    }

    @Test
    public void testCreateUpdateDeleteRead() {

        saveOneEntityForEachTenant();

        TenantContext.setTenantContext(tenant1.getId().toString());
        List<TenantTestEntity> tenantTestEntityList = new ArrayList<>();
        testEntityRepository.findAll().forEach(tenantTestEntityList::add);

        Assertions.assertThat(tenantTestEntityList.size()).isEqualTo(1);
        Assertions.assertThat(tenantTestEntityList.get(0).getTenantId()).isEqualTo(tenant1.getId().toString());

    }

    private void saveOneEntityForEachTenant() {

        for (SharedSchemaTenant tenant: List.of(tenant1, tenant2)) {
            TenantContext.setTenantContext(tenant.getId().toString());
            TenantTestEntity entityTenant1 = new TenantTestEntity.Builder()
                    .id(UUID.randomUUID())
                    .name("Name")
                    .build();
            testEntityRepository.save(entityTenant1);
        }


    }

    @Override
    protected void afterCleanup() {
        // do nothing
    }

    @Override
    protected void beforeCleanup() {

        jdbcTemplate.update("DELETE FROM test_entity_table");
        jdbcTemplate.update("DELETE FROM tenants");

    }
}
