package org.wpstarters.multitenancyspringbootstarter.migrations;

import org.wpstarters.multitenancyspringbootstarter.Tenant;

public interface IMigrationsService {

    void createSchema(String schema);

    void deleteSchema(String schema);

    void runMigrationsOnTenant(Tenant<?> tenant) throws Exception;

    void runMigrationsOnDefaultTenant() throws Exception;

}
