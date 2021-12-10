package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.schemapertenant;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantReadRepository;

import java.util.UUID;

@Repository
public interface SchemaTenantReadRepository
        extends CrudRepository<SchemaTenant, UUID>,
        ITenantReadRepository<SchemaTenant, UUID> {



}
