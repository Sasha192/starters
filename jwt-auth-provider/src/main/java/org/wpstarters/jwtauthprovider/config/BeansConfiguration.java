package org.wpstarters.jwtauthprovider.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wpstarters.commonwebstarter.ITenantIDResolver;
import org.wpstarters.jwtauthprovider.config.conditional.DummyUserVerificationEnabled;
import org.wpstarters.jwtauthprovider.config.conditional.MailUserVerificationEnabled;
import org.wpstarters.jwtauthprovider.config.context.HttpContextHolder;
import org.wpstarters.jwtauthprovider.props.CorsConfigurationProperties;
import org.wpstarters.jwtauthprovider.props.SMTPConfigurationProperties;
import org.wpstarters.jwtauthprovider.props.JksConfigurationProperties;
import org.wpstarters.jwtauthprovider.props.ThrottlingConfigurationProperties;
import org.wpstarters.jwtauthprovider.repository.IRefreshTokenRepository;
import org.wpstarters.jwtauthprovider.repository.UserDetailsRepository;
import org.wpstarters.jwtauthprovider.service.IEncryptionKeys;
import org.wpstarters.jwtauthprovider.service.ITokenService;
import org.wpstarters.jwtauthprovider.service.IUserDetailsService;
import org.wpstarters.jwtauthprovider.service.IUserVerificationService;
import org.wpstarters.jwtauthprovider.service.impl.CustomUserDetailsService;
import org.wpstarters.jwtauthprovider.service.impl.DummmyUserVerificationService;
import org.wpstarters.jwtauthprovider.service.impl.MailUserVerificationService;
import org.wpstarters.jwtauthprovider.service.impl.TokenService;
import org.wpstarters.jwtauthprovider.service.utils.IMailService;
import org.wpstarters.jwtauthprovider.service.utils.MailService;
import org.wpstarters.jwtauthprovider.throttle.IThrottleService;
import org.wpstarters.jwtauthprovider.throttle.ThrottlingTokenServiceWrapper;
import org.wpstarters.jwtauthprovider.throttle.ThrottlingUserVerificationWrapper;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.tenantcrud.shared.NoneSchemaTenant;

import java.util.Objects;
import java.util.Properties;

@Configuration
public class BeansConfiguration {

    private final IThrottleService alwaysTrueThrottleService = new IThrottleService() {
        @Override
        public boolean allow(String fingerPrint) {
            return true;
        }

        @Override
        public void postProcess(String fingerPrint) {
            // do nothing
        }

        @Override
        public void clean(String fingerPrint) {
            // do nothing
        }
    };

    @ConfigurationProperties(prefix = "jks-props")
    @Bean
    public JksConfigurationProperties jksConfigurationProperties() {
        return new JksConfigurationProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "throttling")
    public ThrottlingConfigurationProperties throttlingConfigurationProperties() {
        return new ThrottlingConfigurationProperties();
    }

    @ConfigurationProperties(prefix = "cors-configuration-properties")
    @Bean
    public CorsConfigurationProperties corsConfigurationProperties() {
        return new CorsConfigurationProperties();
    }

    @Bean
    public SMTPConfigurationProperties smtpConfigurationProperties() {
        return new SMTPConfigurationProperties();
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
    public ITokenService jwtUtils(@Value("jwt-properties.issuer") String issuer,
                                  IEncryptionKeys keyPairSupplier,
                                  IRefreshTokenRepository refreshTokenService,
                                  UserDetailsService userDetailsService,
                                  @Autowired(required = false) IThrottleService throttleService) {

        return new ThrottlingTokenServiceWrapper(
                new TokenService(issuer, keyPairSupplier, refreshTokenService, userDetailsService),
                Objects.requireNonNullElse(throttleService, alwaysTrueThrottleService)
        );
    }

    @Bean
    @Conditional(value = DummyUserVerificationEnabled.class)
    public IUserVerificationService dummyVerificationService(@Autowired(required = false) IThrottleService throttleService) {

        return new ThrottlingUserVerificationWrapper(
                new DummmyUserVerificationService(),
                Objects.requireNonNullElse(throttleService, alwaysTrueThrottleService)
        );

    }

    @Bean
    @Conditional(value = MailUserVerificationEnabled.class)
    public IUserVerificationService verificationService(@Autowired(required = false) IThrottleService throttleService,
                                                        @Autowired IMailService mailService) {

        return new ThrottlingUserVerificationWrapper(
                new MailUserVerificationService(mailService),
                Objects.requireNonNullElse(throttleService, alwaysTrueThrottleService)
        );

    }


    @Bean
    @Primary
    public ITenantIDResolver<NoneSchemaTenant> noneSchemaTenantITenantIDResolver(StarterConfigurationProperties properties) {
        return (tenant) -> properties.getDefaultSchema();
    }

    @Bean
    public HandlerInterceptor httpContextHolder() {
        return new HttpContextHolder();
    }

    @Bean
    public JavaMailSender getJavaMailSender(SMTPConfigurationProperties smtpProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpProperties.getHost());
        mailSender.setPort(smtpProperties.getPort());

        mailSender.setUsername(smtpProperties.getUsername());
        mailSender.setPassword(smtpProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.putAll(smtpProperties.getProperties());

        return mailSender;
    }

    @Bean
    public MailService mailService(JavaMailSender javaMailSender) {
        return new MailService(javaMailSender);
    }

}
