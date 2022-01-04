package org.wpstarters.jwtauthprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@SpringBootApplication
@EnableJpaRepositories
public class JwtAuthProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthProviderApplication.class, args);
	}

}
