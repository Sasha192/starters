package org.wpstarters.multitenancyspringbootstarter.migrations;

import org.wpstarters.commonwebstarter.Tenant;

public interface ITenantsManipulator<T> {

    Tenant<T> createTenant();

    String removeTenant(Tenant<T> tenant);

}
