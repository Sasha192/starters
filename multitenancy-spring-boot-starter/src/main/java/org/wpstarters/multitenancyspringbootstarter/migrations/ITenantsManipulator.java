package org.wpstarters.multitenancyspringbootstarter.migrations;

import org.wpstarters.multitenancyspringbootstarter.Tenant;

public interface ITenantsManipulator<T> {

    Tenant<T> createTenant();

    String removeTenant(Tenant<T> tenant);

}
