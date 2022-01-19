package org.wpstarters.jwtauthprovider.config.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.apache.commons.lang3.StringUtils.lowerCase;

public class ThrottlingEnabled implements Condition {


    private static final String THROTTLING_ENABLED_PROP = "throttling.enabled";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final String value = lowerCase(context.getEnvironment().getProperty(THROTTLING_ENABLED_PROP));
        return "true".equals(value);
    }
}
