package org.wpstarters.multitenancyspringbootstarter.migrations;

import org.wpstarters.commonwebstarter.Tenant;

public interface IMigrationsService<T extends Tenant<?>> {

    void createSchema(String schema);

    void deleteSchema(String schema);

    void runMigrationsOnTenant(T tenant) throws Exception;

    void runMigrationsOnDefaultTenant() throws Exception;

}
