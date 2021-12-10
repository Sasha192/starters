package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.wpstarters.commonwebstarter.Tenant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class SharedSchemaTenant implements Tenant<UUID> {

    @Id
    private UUID id;
    private boolean active;

    public SharedSchemaTenant(UUID id, boolean active) {
        this.id = id;
        this.active = active;
    }

    public SharedSchemaTenant() {
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SharedSchemaTenant that = (SharedSchemaTenant) o;

        return new EqualsBuilder().append(isActive(), that.isActive()).append(getId(), that.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(isActive()).toHashCode();
    }
}
