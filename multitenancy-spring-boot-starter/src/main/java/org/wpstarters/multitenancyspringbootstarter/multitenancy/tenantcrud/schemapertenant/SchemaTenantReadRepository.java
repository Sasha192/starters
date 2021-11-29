package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant;

import org.springframework.data.repository.CrudRepository;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantReadRepository;

import java.util.UUID;

public interface SchemaTenantReadRepository
        extends CrudRepository<SchemaTenant, UUID>,
        ITenantReadRepository<SchemaTenant, UUID> {



}
