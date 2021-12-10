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

import java.util.Optional;
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

    ThreadLocal<SharedSchemaTenant> schemaTenantThreadLocal = new ThreadLocal<>();

    @BeforeEach
    public void init() {

        SharedSchemaTenant newTenant = (SharedSchemaTenant) tenantManagementService.createTenant();
        schemaTenantThreadLocal.set(newTenant);

    }

    @Test
    public void testCreateUpdateDeleteRead() {

        TenantTestEntity testEntity = new TenantTestEntity.Builder()
                .id(UUID.randomUUID())
                .name("Name")
                .build();

        TenantContext.setTenantContext(schemaTenantThreadLocal.get().getId().toString());
        testEntity = testEntityRepository.save(testEntity);

        Optional<TenantTestEntity> tenantTestEntityOptional = testEntityRepository.findById(testEntity.getId());
        Assertions.assertThat(tenantTestEntityOptional.get().getName()).isEqualTo("Name");
        Assertions.assertThat(tenantTestEntityOptional.get().getTenantId()).isEqualTo(schemaTenantThreadLocal.get().getId().toString());
        testEntity.setName("Another name");
        testEntityRepository.save(testEntity);
        testEntityRepository.deleteById(testEntity.getId());


        Assertions.assertThat(testEntityRepository.findById(testEntity.getId()).isEmpty()).isTrue();

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
