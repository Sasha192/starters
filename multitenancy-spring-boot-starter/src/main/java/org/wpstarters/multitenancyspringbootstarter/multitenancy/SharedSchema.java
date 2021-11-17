package org.wpstarters.multitenancyspringbootstarter.multitenancy;

public class SharedSchema extends MultitenancyConditional {

    @Override
    protected Multitenancy getMultitenancy() {
        return Multitenancy.SHARED_DATABASE;
    }
}
