package org.wpstarters37.authorizationstarter.configuration.props;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(value = "wp37-authorization-starter.security.cors.oauth",
        ignoreUnknownFields = false)
public class OauthCorsConfigurationProperties extends CorsConfigurationProperties {


}
