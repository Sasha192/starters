package org.wpstarters37.authorizationstarter.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.wpstarters37.authorizationstarter.configuration.props.CorsConfigurationProperties;
import org.wpstarters37.authorizationstarter.configuration.userdetails.UnsupportedUserDetailsService;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collections;

@Configuration
@AutoConfigureBefore(value = {
        WebSecurityServerConfig.class,
        ApplicationAuthorizationServerConfig.class
})
public class SecurityBeansConfiguration {

    public static final String OAUTH_MATCHER = "/oauth/**";

    private final UserDetailsService userDetailsService;

    public SecurityBeansConfiguration() {
        this.userDetailsService = new UnsupportedUserDetailsService();
    }

    public static CorsConfigurationSource getCorsConfigurationSource(CorsConfigurationProperties corsProperties, String urlMatcher) {
        CorsConfiguration apiCorsConfiguration = new CorsConfiguration();
        apiCorsConfiguration.setAllowedOriginPatterns(corsProperties.getOrigins());
        apiCorsConfiguration.setAllowedMethods(corsProperties.getMethods());
        apiCorsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(urlMatcher, apiCorsConfiguration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean(BeanIds.USER_DETAILS_SERVICE)
    @Primary
    public UserDetailsService userDetailsServiceBean() {
        return userDetailsService;
    }

}
