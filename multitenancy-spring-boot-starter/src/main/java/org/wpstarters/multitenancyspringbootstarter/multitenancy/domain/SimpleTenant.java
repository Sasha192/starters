package org.wpstarters.multitenancyspringbootstarter.multitenancy.domain;

import org.wpstarters.multitenancyspringbootstarter.Tenant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.StringJoiner;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class SimpleTenant implements Tenant<UUID> {

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

    @Override
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

        public SimpleTenant build() {
            SimpleTenant simpleTenant = new SimpleTenant();
            simpleTenant.setId(id);
            simpleTenant.setSchema(schema);
            simpleTenant.setActive(active);
            return simpleTenant;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SimpleTenant.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("schema='" + schema + "'")
                .add("active=" + active)
                .toString();
    }
}
