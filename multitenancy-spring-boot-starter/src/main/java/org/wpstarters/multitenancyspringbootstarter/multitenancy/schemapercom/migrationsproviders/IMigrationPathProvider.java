package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom.migrationsproviders;

import java.net.URI;
import java.util.List;

public interface IMigrationPathProvider {

    List<URI> tenantsMigrationsPaths();

    List<URI> defaultSchemaMigrationsPaths();

}
