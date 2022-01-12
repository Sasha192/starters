package org.wpstarters.multitenancyspringbootstarter.multitenancy;

public class NoneTenants extends MultitenancyConditional {

    @Override
    protected Multitenancy getMultitenancy() {
        return Multitenancy.NONE;
    }
}
