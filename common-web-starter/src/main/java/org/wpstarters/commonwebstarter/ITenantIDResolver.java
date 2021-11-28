package org.wpstarters.commonwebstarter;

public interface ITenantIDResolver<T extends Tenant<?>> {

    String resolveTenant(String tenantId);

}
