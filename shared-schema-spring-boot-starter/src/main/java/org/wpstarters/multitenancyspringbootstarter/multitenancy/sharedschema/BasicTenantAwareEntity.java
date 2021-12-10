package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@FilterDef(name = "tenantFilter",
        parameters = {
        @ParamDef(name = "tenantId", type = "string")},
        defaultCondition = "tenant_id=:tenantId")
@Filter(name = "tenantFilter")
@EntityListeners(BasicTenantListener.class)
public abstract class BasicTenantAwareEntity implements ITenantAware<String> {

    private String tenantId;

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
