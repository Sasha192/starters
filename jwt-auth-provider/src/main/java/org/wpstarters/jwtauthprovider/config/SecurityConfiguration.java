package org.wpstarters.jwtauthprovider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.wpstarters.jwtauthprovider.config.entrypoint.AuthenticationEntryPointJwt;
import org.wpstarters.jwtauthprovider.config.filters.AuthenticationTokenFilter;
import org.wpstarters.jwtauthprovider.props.CorsConfigurationProperties;
import org.wpstarters.jwtauthprovider.service.IEncryptionKeys;
import org.wpstarters.jwtauthprovider.service.IRefreshTokenRepository;
import org.wpstarters.jwtauthprovider.service.impl.TokenService;
import org.wpstarters.jwtauthprovider.service.impl.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationEntryPointJwt unauthorizedHandler;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final TokenService tokenService;
    private final CorsConfigurationProperties corsConfigurationProperties;

    public SecurityConfiguration(CustomUserDetailsService userDetailsService,
                                 PasswordEncoder passwordEncoder,
                                 TokenService tokenService,
                                 CorsConfigurationProperties corsConfigurationProperties,
                                 ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = new AuthenticationEntryPointJwt();
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
        this.corsConfigurationProperties = corsConfigurationProperties;
    }

    @Bean
    public TokenService jwtUtils(@Value("jwt-properties.issuer") String issuer,
                                 IEncryptionKeys keyPairSupplier,
                                 IRefreshTokenRepository refreshTokenService,
                                 ObjectMapper objectMapper,
                                 UserDetailsService userDetailsService) {
        return new TokenService(
                issuer,
                keyPairSupplier,
                refreshTokenService,
                userDetailsService,
                objectMapper);
    }

    public AuthenticationTokenFilter authenticationJwtTokenFilter(TokenService tokenService,
                                                                  CustomUserDetailsService userDetailsService,
                                                                  ObjectMapper objectMapper) {
        return new AuthenticationTokenFilter(tokenService, userDetailsService, objectMapper);
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(configurationSourceCors(corsConfigurationProperties))
                .and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(
                authenticationJwtTokenFilter(tokenService, userDetailsService, objectMapper),
                UsernamePasswordAuthenticationFilter.class
        );
    }

    public CorsConfigurationSource configurationSourceCors(CorsConfigurationProperties corsConfigurationProperties) {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(corsConfigurationProperties.getAllowedOriginPatterns());
        corsConfiguration.addAllowedMethod(corsConfigurationProperties.getAllowedMethods());
        corsConfiguration.setAllowedHeaders(corsConfigurationProperties.getAllowedHeaders());


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsConfigurationProperties.getUrlPattern(), corsConfiguration);
        return source;
    }
}
