package org.wpstarters37.authorizationstarter.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.web.cors.CorsConfigurationSource;
import org.wpstarters37.authorizationstarter.configuration.props.ContentSecurityPolicyConfigurationProperties;
import org.wpstarters37.authorizationstarter.configuration.props.OauthCorsConfigurationProperties;

@Configuration
@Import(value = {OauthCorsConfigurationProperties.class,
        ContentSecurityPolicyConfigurationProperties.class})
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityServerConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final OauthCorsConfigurationProperties corsConfigurationProperties;
    private final UserDetailsService userDetailsService;
    private final ContentSecurityPolicyConfigurationProperties cspConfigurationProperties;

    public WebSecurityServerConfig(PasswordEncoder passwordEncoder,
                                   OauthCorsConfigurationProperties corsConfigurationProperties,
                                   UserDetailsService userDetailsService,
                                   @Autowired(required = false) ContentSecurityPolicyConfigurationProperties cspConfigurationProperties) {
        this.corsConfigurationProperties = corsConfigurationProperties;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.cspConfigurationProperties = cspConfigurationProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                    .authorizeRequests()
                        .anyRequest()
                        .permitAll()
                    .and()
                    .requestMatchers(oauthConfigurer -> oauthConfigurer.mvcMatchers(SecurityBeansConfiguration.OAUTH_MATCHER))
                    .csrf()
                        .disable()
                    .cors()
                        .configurationSource(corsConfigurationSource())
                    .and()
                    .requiresChannel()
                        .antMatchers(SecurityBeansConfiguration.OAUTH_MATCHER).requiresSecure()
                    .and()
                    .headers()
                        .httpStrictTransportSecurity()
                            .maxAgeInSeconds(31104000)
                            .includeSubDomains(true);

        if (cspConfigurationProperties != null && StringUtils.isNotBlank(cspConfigurationProperties.getPolicy())) {
            http.requestMatchers()
                    .mvcMatchers(SecurityBeansConfiguration.OAUTH_MATCHER)
                    .and()
                    .headers()
                    .contentSecurityPolicy(cspConfigurationProperties.getPolicy());
        }

    }

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @ConditionalOnProperty(value = "37wp-authorization-starter.authentication-manager.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    @ConditionalOnProperty(value = "37wp-authorization-starter.authentication-manager.enabled",
            havingValue = "true",
            matchIfMissing = true)
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return SecurityBeansConfiguration.getCorsConfigurationSource(corsConfigurationProperties, SecurityBeansConfiguration.OAUTH_MATCHER);
    }
}
