package org.wpstarters.multitenancyspringbootstarter.multitenancy;

public class SchemaPerTenant extends MultitenancyConditional {


    @Override
    protected Multitenancy getMultitenancy() {
        return Multitenancy.SCHEMA_PER_TENANT;
    }
}
