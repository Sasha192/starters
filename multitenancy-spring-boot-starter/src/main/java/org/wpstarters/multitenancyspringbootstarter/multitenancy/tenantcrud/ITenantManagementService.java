package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud;

import org.wpstarters.commonwebstarter.Tenant;

public interface ITenantManagementService<ID> {
    Tenant<ID> createTenant();
}
