package org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud;

import org.wpstarters.commonwebstarter.Tenant;

import java.util.Optional;

public interface ITenantReadRepository<T extends Tenant<ID>, ID> {

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    Iterable<T> findAll();

    long count();

}
