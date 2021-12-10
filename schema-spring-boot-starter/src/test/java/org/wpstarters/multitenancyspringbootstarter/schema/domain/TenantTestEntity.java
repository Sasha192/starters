package org.wpstarters.multitenancyspringbootstarter.schema.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "tenant_test_table")
public class TenantTestEntity {

    @Id
    private UUID id;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }


    public static final class Builder {
        private UUID id;
        private String name;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public TenantTestEntity build() {
            TenantTestEntity tenantTestEntity = new TenantTestEntity();
            tenantTestEntity.setId(id);
            tenantTestEntity.setName(name);
            return tenantTestEntity;
        }
    }
}
