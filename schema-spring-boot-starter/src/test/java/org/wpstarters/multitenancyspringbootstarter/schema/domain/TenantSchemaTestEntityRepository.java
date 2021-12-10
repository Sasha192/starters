package org.wpstarters.multitenancyspringbootstarter.schema.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TenantSchemaTestEntityRepository extends CrudRepository<TenantTestEntity, UUID> {

}
