package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wpstarters.commonwebstarter.Tenant;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantManagementService;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant.SchemaTenant;

import javax.annotation.CheckReturnValue;
import java.util.UUID;

public class SharedSchemaTenantManagementService implements ITenantManagementService<UUID> {

    private static final Logger logger = LoggerFactory.getLogger(SharedSchemaTenantManagementService.class);

    private final SharedSchemaTenantReadRepository tenantRepository;

    public SharedSchemaTenantManagementService(SharedSchemaTenantReadRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    /**
     * @return fully initialized tenant or empty Tenant - means something bad happened
     */
    @Override
    @CheckReturnValue
    public Tenant<UUID> createTenant() {

        SharedSchemaTenant tenant = new SharedSchemaTenant(UUID.randomUUID(), true);

        try {

            tenant = tenantRepository.save(tenant);
            return tenant;

        } catch (Exception e) {

            logger.error("Exception occurred while running Migrations on {} tenant", tenant.getId(), e);

        }

        return new SchemaTenant();
    }

}
