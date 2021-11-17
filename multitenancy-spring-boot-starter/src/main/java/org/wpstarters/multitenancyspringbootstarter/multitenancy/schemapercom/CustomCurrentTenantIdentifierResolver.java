package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.wpstarters.commonwebstarter.TenantContext;

import java.util.function.Predicate;


public class CustomCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final Predicate<String> enabledTenant = (tenant -> true);

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getTenantContext();
        if(StringUtils.isNotBlank(tenant) && enabledTenant.test(tenant)) {
            return tenant;
        }
        throw new IllegalArgumentException("Incorrect tenant name");
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
