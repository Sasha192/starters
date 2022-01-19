package org.wpstarters.commonwebstarter.tenant;

public interface ITenantIDResolver<T extends Tenant<?>> {

    String resolveTenant(String tenantId);

}
