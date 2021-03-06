package org.wpstarters.multitenancyspringbootstarter.shared.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TenantSharedTestEntityRepository extends JpaRepository<TenantTestEntity, UUID> {

}
