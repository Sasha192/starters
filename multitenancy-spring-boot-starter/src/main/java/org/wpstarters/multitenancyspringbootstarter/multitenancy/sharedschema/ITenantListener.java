package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

public interface ITenantListener<ID> {

    void setTenantId(ITenantAware<ID> tenantAware);

}
