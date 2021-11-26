package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud;

import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface SchemaTenantRepository extends CrudRepository<SchemaTenant, UUID> {



}
