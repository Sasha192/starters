package org.wpstarters.jwtauthprovider;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "jwt-auth-properties")
public class JwtAuthenticationProviderProperties {

    private long jwtExpirationTimeInMs;

    private String issuer;

    public long getJwtExpirationTimeInMs() {
        return jwtExpirationTimeInMs;
    }

    public void setJwtExpirationTimeInMs(long jwtExpirationTimeInMs) {
        this.jwtExpirationTimeInMs = jwtExpirationTimeInMs;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
