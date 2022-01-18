package org.wpstarters.jwtauthprovider.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.lowerCase;

public class ThrottlingEnabledCondition implements Condition {

    private static final String THROTTLING_ENABLED = "throttling-enabled";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final String value = lowerCase(Objects.toString(context.getEnvironment().getProperty(THROTTLING_ENABLED)));
        return "true".equals(value);
    }
}
