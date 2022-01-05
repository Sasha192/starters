package org.wpstarters.jwtauthprovider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wpstarters.commonwebstarter.ITenantIDResolver;
import org.wpstarters.jwtauthprovider.props.CorsConfigurationProperties;
import org.wpstarters.jwtauthprovider.props.JksConfigurationProperties;
import org.wpstarters.jwtauthprovider.repository.IRefreshTokenRepository;
import org.wpstarters.jwtauthprovider.repository.UserDetailsRepository;
import org.wpstarters.jwtauthprovider.service.IEncryptionKeys;
import org.wpstarters.jwtauthprovider.service.IUserDetailsService;
import org.wpstarters.jwtauthprovider.service.impl.CustomUserDetailsService;
import org.wpstarters.jwtauthprovider.service.impl.TokenService;
import org.wpstarters.multitenancyspringbootstarter.MultitenancyStarterConfiguration;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.NoneSchemaTenant;

@Configuration
public class BeansConfiguration {

    @ConfigurationProperties(prefix = "jks-props")
    @Bean
    public JksConfigurationProperties jksConfigurationProperties() {
        return new JksConfigurationProperties();
    }

    @ConfigurationProperties(prefix = "cors-configuration-properties")
    @Bean
    public CorsConfigurationProperties corsConfigurationProperties() {
        return new CorsConfigurationProperties();
    }

    @Bean
    public IUserDetailsService userDetailsService(UserDetailsRepository repository, JdbcTemplate jdbcTemplate, PasswordEncoder encoder, ObjectMapper objectMapper) {
        return new CustomUserDetailsService(repository, jdbcTemplate, encoder, objectMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B);
    }

    @Bean
    public TokenService jwtUtils(@Value("jwt-properties.issuer") String issuer,
                                 IEncryptionKeys keyPairSupplier,
                                 IRefreshTokenRepository refreshTokenService,
                                 UserDetailsService userDetailsService) {
        return new TokenService(issuer, keyPairSupplier, refreshTokenService, userDetailsService);
    }

    @Bean
    public ITenantIDResolver<NoneSchemaTenant> noneSchemaTenantITenantIDResolver(StarterConfigurationProperties properties) {
        return (tenant) -> properties.getDefaultSchema();
    }


}
