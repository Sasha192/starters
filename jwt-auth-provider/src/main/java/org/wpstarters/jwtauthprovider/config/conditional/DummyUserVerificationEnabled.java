package org.wpstarters.jwtauthprovider.config.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.apache.commons.lang3.StringUtils.lowerCase;

public class DummyUserVerificationEnabled implements Condition {

    private static final String TEST_ENVIRONMENT = "test-environment.enabled";
    private static final String DUMMY_USER_VERIFICATION = "user-verification";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final String testEnvironmentEnabled = lowerCase(context.getEnvironment().getProperty(TEST_ENVIRONMENT));
        final String dummyUserVerification = lowerCase(context.getEnvironment().getProperty(DUMMY_USER_VERIFICATION));
        return "true".equals(testEnvironmentEnabled) && "dummy".equals(dummyUserVerification);
    }
}
