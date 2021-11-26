package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud;

import org.wpstarters.commonwebstarter.Tenant;

import javax.persistence.Column;
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
    @Column(name = "id")
    private UUID id;

    @Size(max = 32)
    @Column(name = "schema")
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
}
