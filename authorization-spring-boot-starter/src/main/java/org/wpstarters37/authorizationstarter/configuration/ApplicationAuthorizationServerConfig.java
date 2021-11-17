package org.wpstarters37.authorizationstarter.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.wpstarters37.authorizationstarter.configuration.props.OauthCorsConfigurationProperties;

import javax.sql.DataSource;

import static org.wpstarters37.authorizationstarter.configuration.SecurityBeansConfiguration.OAUTH_MATCHER;

@Configuration
@Import(value = {OauthCorsConfigurationProperties.class})
@ConditionalOnBean(value = {DataSource.class})
public class ApplicationAuthorizationServerConfig
        extends AuthorizationServerConfigurerAdapter {

    private final DataSource dataSource;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JdbcClientDetailsService jdbcClientDetailsService;
    private final OauthCorsConfigurationProperties corsConfigurationProperties;
    private final PasswordEncoder passwordEncoder;

    public ApplicationAuthorizationServerConfig(DataSource ds,
                                                PasswordEncoder passwordEncoder,
                                                AuthenticationManager authMgr,
                                                UserDetailsService usrSvc,
                                                OauthCorsConfigurationProperties corsConfigurationProperties) {
        this.dataSource = ds;
        this.authenticationManager = authMgr;
        this.userDetailsService = usrSvc;
        this.corsConfigurationProperties = corsConfigurationProperties;
        this.jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        this.passwordEncoder = passwordEncoder;
        jdbcClientDetailsService.setPasswordEncoder(this.passwordEncoder);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer cfg)  {
        setUpFilters(cfg);
        cfg.checkTokenAccess("permitAll")
                .passwordEncoder(this.passwordEncoder)
                .allowFormAuthenticationForClients();
    }

    private void setUpFilters(AuthorizationServerSecurityConfigurer cfg) {
        CorsFilter filter = corsFilter();
        cfg.addTokenEndpointAuthenticationFilter(filter);
    }

    private CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.withClientDetails(jdbcClientDetailsService);
    }

    @Bean
    public ClientRegistrationService clientRegistrationService() {
        return jdbcClientDetailsService;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore());
        endpoints.authenticationManager(authenticationManager);
        endpoints.userDetailsService(userDetailsService);
    }

    private CorsConfigurationSource corsConfigurationSource() {
        return SecurityBeansConfiguration.getCorsConfigurationSource(corsConfigurationProperties, OAUTH_MATCHER);
    }
}
