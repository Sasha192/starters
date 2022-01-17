package org.wpstarters.commonwebstarter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wpstarters.commonwebstarter.tenant.ITenantIDResolver;
import org.wpstarters.commonwebstarter.tenant.Tenant;
import org.wpstarters.commonwebstarter.tenant.TenantContextInterceptor;

@Configuration
public class CommonWebStarterAutoConfiguration implements WebMvcConfigurer {

    private final ITenantIDResolver<? extends Tenant<?>> tenantIDResolver;

    public CommonWebStarterAutoConfiguration(ITenantIDResolver<? extends Tenant<?>> tenantIDResolver) {
        this.tenantIDResolver = tenantIDResolver;
    }

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantContextInterceptor(tenantIDResolver));
    }


}
