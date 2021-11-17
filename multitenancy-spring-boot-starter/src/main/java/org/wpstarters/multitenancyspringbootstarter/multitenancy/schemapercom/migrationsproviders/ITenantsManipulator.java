package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders;

import org.wpstarters.multitenancyspringbootstarter.Tenant;

public interface ITenantsManipulator<T> {

    Tenant<T> createTenant();

    String removeTenant(Tenant<T> tenant);

}
