package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared;

import org.springframework.data.repository.CrudRepository;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.ITenantReadRepository;

import java.util.UUID;

public interface SharedSchemaTenantReadRepository extends CrudRepository<SharedSchemaTenant, UUID>,
        ITenantReadRepository<SharedSchemaTenant, UUID> {



}
