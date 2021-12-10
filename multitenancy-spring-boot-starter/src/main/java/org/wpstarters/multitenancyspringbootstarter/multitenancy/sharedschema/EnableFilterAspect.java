package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.wpstarters.commonwebstarter.TenantContext;

import javax.persistence.EntityManager;
import java.util.Objects;

@Aspect
@Component
public class EnableFilterAspect {

    @AfterReturning(pointcut="bean(defaultEntityManagerFactory) && execution(* createEntityManager(..))", returning="retVal")
    public void getSessionAfter(JoinPoint joinPoint, Object retVal) {
        if (retVal instanceof EntityManager && Objects.nonNull(TenantContext.getTenantContext())) {
            Session session = ((EntityManager) retVal).unwrap(Session.class);
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId",TenantContext.getTenantContext());
        }
    }

}
