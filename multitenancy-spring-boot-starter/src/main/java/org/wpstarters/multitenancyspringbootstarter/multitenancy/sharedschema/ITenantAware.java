package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

public interface ITenantAware<ID> {

    ID getTenantId();

    void setTenantId(ID tenantId);

}
