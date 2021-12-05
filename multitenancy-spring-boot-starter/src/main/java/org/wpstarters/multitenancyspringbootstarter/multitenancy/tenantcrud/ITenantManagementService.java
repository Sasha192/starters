package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud;

import org.wpstarters.commonwebstarter.Tenant;

public interface ITenantManagementService<ID, T extends Tenant<ID>> {
    Tenant<ID> createTenant();

    Tenant<ID> removeTenant(T tenant);
}
