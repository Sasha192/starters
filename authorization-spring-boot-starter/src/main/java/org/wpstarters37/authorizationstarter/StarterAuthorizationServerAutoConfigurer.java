package org.wpstarters37.authorizationstarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.wpstarters37.authorizationstarter.api.RegistrationController;
import org.wpstarters37.authorizationstarter.configuration.ApplicationAuthorizationServerConfig;
import org.wpstarters37.authorizationstarter.configuration.BeanConfiguration;
import org.wpstarters37.authorizationstarter.configuration.SecurityBeansConfiguration;
import org.wpstarters37.authorizationstarter.configuration.WebSecurityServerConfig;
import org.wpstarters37.authorizationstarter.domain.services.UserVerificationService;

/**
 *
 * NOW:
 *
 * 	Application is in charge of:
 *  - migrations should be defined inside application, that uses this starter
 *  - Datasource is delivered from the application, that uses this starter
 *
 *
 *  Starter is:
 *
 *   - JDBC stored users and clients
 *   - CORS
 *   - WebSecurity for OAUTH Endpoints
 *   - Google, Facebook, Telegram authorization tokens
 *
 *
 *
 *   Later:
 *
 *    - Throttling
 *    - More customized starter
 *    - Implementation of clients according to the OAUTH2.1 Framework
 *
 *
 *
 */
@EnableAuthorizationServer
@EnableConfigurationProperties
@Import(value = {
		ApplicationAuthorizationServerConfig.class,
		WebSecurityServerConfig.class,
		SecurityBeansConfiguration.class,
		BeanConfiguration.class
})
public class StarterAuthorizationServerAutoConfigurer {

}
