package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.wpstarters.commonwebstarter.Tenant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class SchemaTenant implements Tenant<UUID> {

    @Id
    private UUID id;

    @Size(max = 32)
    private String schema;

    private boolean active;

    @Override
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public static final class Builder {
        private UUID id;
        private String schema;
        private boolean active;

        public Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder schema(String schema) {
            this.schema = schema;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public SchemaTenant build() {
            SchemaTenant schemaTenant = new SchemaTenant();
            schemaTenant.setId(id);
            schemaTenant.setSchema(schema);
            schemaTenant.setActive(active);
            return schemaTenant;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SchemaTenant.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("schema='" + schema + "'")
                .add("active=" + active)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SchemaTenant that = (SchemaTenant) o;

        return new EqualsBuilder().append(isActive(), that.isActive()).append(getId(), that.getId()).append(getSchema(), that.getSchema()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getId()).append(getSchema()).append(isActive()).toHashCode();
    }
}
