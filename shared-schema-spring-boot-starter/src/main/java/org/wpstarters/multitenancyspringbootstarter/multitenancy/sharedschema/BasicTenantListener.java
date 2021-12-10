package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

import org.springframework.stereotype.Component;
import org.wpstarters.commonwebstarter.TenantContext;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.Objects;

@Component
public class BasicTenantListener implements ITenantListener<String> {

    @Override
    @PrePersist
    @PreUpdate
    @PreRemove
    public void setTenantId(ITenantAware<String> tenantAware) {

        String tenantId = TenantContext.getTenantContext();

        Objects.requireNonNull(tenantId);

        tenantAware.setTenantId(tenantId);

    }
}
