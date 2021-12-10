package org.wpstarters.multitenancyspringbootstarter.migrations;

import java.net.URI;
import java.util.List;

public interface IMigrationPathProvider {

    List<URI> tenantsMigrationsPaths();

    List<URI> defaultMigrationsPaths();

}
