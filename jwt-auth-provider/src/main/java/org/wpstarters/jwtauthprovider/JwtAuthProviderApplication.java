package org.wpstarters.jwtauthprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class JwtAuthProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtAuthProviderApplication.class, args);
	}

}
