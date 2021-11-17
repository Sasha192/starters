package org.wpstarters.multitenancyspringbootstarter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as=Tenant.class)
public interface Tenant<ID> {

    ID getId();

    String getSchema();

    boolean isActive();

}
