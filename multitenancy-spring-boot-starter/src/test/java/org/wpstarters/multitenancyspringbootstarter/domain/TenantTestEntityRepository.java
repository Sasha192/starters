package org.wpstarters.multitenancyspringbootstarter.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TenantTestEntityRepository extends CrudRepository<TenantTestEntity, UUID> {

}
