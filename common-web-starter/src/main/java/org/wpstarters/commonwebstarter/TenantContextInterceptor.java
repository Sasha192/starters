package org.wpstarters.commonwebstarter;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class TenantContextInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER_NAME = "X-TENANT-ID";

    private final ITenantIDResolver<? extends Tenant<?>> tenantIDResolver;

    public TenantContextInterceptor(ITenantIDResolver<? extends Tenant<?>> tenantIDResolver) {
        this.tenantIDResolver = tenantIDResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String tenantId = request.getHeader(TENANT_HEADER_NAME);
        if (tenantId != null) {
            String tenant = tenantIDResolver.resolveTenant(tenantId);
            TenantContext.setTenantContext(tenant);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        TenantContext.clearTenantContext();
    }
}
