package org.wpstarters.multitenancyspringbootstarter.multitenancy.domain;

import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface SimpleTenantRepository extends CrudRepository<SimpleTenant, UUID> {
}
