package org.wpstarters.multitenancyspringbootstarter.multitenancy.domain;

import org.wpstarters.multitenancyspringbootstarter.Tenant;

public interface ITenantManagementService<ID> {
    Tenant<ID> createTenant();
}
