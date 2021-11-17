package org.wpstarters37.authorizationstarter.configuration.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "wp37-authorization-starter.security.content-security-policy",
        ignoreUnknownFields = false)
public class ContentSecurityPolicyConfigurationProperties {

    private String policy;

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }
}
