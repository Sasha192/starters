package org.wpstarters.jwtauthprovider.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors-configuration-properties")
public class CorsConfigurationProperties {

    private List<String> allowedOriginPatterns;
    private String allowedMethods;
    private List<String> allowedHeaders;
    private String urlPattern;

    public List<String> getAllowedOriginPatterns() {
        return allowedOriginPatterns;
    }

    public String getAllowedMethods() {
        return allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setAllowedOriginPatterns(List<String> allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }
}
