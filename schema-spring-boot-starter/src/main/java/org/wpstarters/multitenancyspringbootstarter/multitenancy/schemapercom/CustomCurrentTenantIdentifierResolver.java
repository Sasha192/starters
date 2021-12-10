package org.wpstarters.multitenancyspringbootstarter.multitenancy.schemapercom;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.wpstarters.commonwebstarter.TenantContext;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;

import java.util.function.Predicate;


public class CustomCurrentTenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    private static final Predicate<String> ENABLED_TENANT = (tenant -> true);

    private final StarterConfigurationProperties starterConfigurationProperties;
    private final Predicate<String> enabledTenant;

    public CustomCurrentTenantIdentifierResolver(StarterConfigurationProperties starterConfigurationProperties,
                                                 @Autowired(required = false) Predicate<String> enabledTenantCandidate) {
        this.starterConfigurationProperties = starterConfigurationProperties;
        this.enabledTenant = enabledTenantCandidate != null ? enabledTenantCandidate: ENABLED_TENANT;

    }

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getTenantContext();
        if(StringUtils.isNotBlank(tenant) && enabledTenant.test(tenant)) {
            return tenant;
        }
        return starterConfigurationProperties.getDefaultSchema();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
