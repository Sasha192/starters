package org.wpstarters.multitenancyspringbootstarter.multitenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.upperCase;
import static org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties.MULTITENANCY_PROPERTY;

public abstract class MultitenancyConditional implements Condition {

    private static final Logger logger = LoggerFactory.getLogger(MultitenancyConditional.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final String value = upperCase(
                Objects.toString(context.getEnvironment().getProperty(MULTITENANCY_PROPERTY),
                        Multitenancy.SCHEMA_PER_TENANT.toString())
        );
        try {
            return Multitenancy.valueOf(value) == getMultitenancy();
        } catch (IllegalArgumentException exception) {
            logger.error("Incorrect value passed to {} property: {}", MULTITENANCY_PROPERTY, value);
            throw exception;
        }
    }

    protected abstract Multitenancy getMultitenancy();

}
