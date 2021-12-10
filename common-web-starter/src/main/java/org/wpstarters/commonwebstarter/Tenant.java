package org.wpstarters.commonwebstarter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as=Tenant.class)
public interface Tenant<ID> {

    ID getId();

    boolean isActive();

    /**
     * @return null or schema-name
     */
    String getSchema();

}
