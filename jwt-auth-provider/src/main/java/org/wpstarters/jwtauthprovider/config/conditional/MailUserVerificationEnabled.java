package org.wpstarters.jwtauthprovider.config.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.apache.commons.lang3.StringUtils.lowerCase;

public class MailUserVerificationEnabled implements Condition {

    private static final String GMAIL_USER_VERIFICATION = "user-verification";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        final String dummyUserVerification = lowerCase(context.getEnvironment().getProperty(GMAIL_USER_VERIFICATION));
        return "mail".equals(dummyUserVerification);
    }
}
