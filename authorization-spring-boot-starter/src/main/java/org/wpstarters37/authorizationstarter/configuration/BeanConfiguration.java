package org.wpstarters37.authorizationstarter.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.wpstarters37.authorizationstarter.api.RegistrationController;
import org.wpstarters37.authorizationstarter.domain.services.AlwaysTrueUserVerificationService;
import org.wpstarters37.authorizationstarter.domain.services.UserVerificationService;

@Configuration
@AutoConfigureAfter(value = {
        ApplicationAuthorizationServerConfig.class,
        SecurityBeansConfiguration.class,
        WebSecurityServerConfig.class
})
public class BeanConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public RegistrationController registrationController(ClientDetailsService clientDetailsService,
                                                         ClientRegistrationService clientRegistrationService,
                                                         PasswordEncoder passwordEncoder,
                                                         TokenStore tokenStore,
                                                         UserVerificationService verificationService) {
        return new RegistrationController(clientDetailsService, clientRegistrationService, passwordEncoder, tokenStore, verificationService);
    }

    @ConditionalOnMissingBean
    @Bean
    public UserVerificationService verificationService() {
        return new AlwaysTrueUserVerificationService();
    }


}
