package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders;

import org.wpstarters.multitenancyspringbootstarter.Tenant;

public interface IMigrationsService {

    void createSchema(String schema);

    void deleteSchema(String schema);

    void runMigrationsOnTenant(Tenant<?> tenant) throws Exception;

    void runMigrationsOnDefaultTenant() throws Exception;

}
