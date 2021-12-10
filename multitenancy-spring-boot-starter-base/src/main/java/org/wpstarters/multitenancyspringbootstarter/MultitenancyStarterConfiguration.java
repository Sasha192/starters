package org.wpstarters.multitenancyspringbootstarter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.CustomLiquibaseProperties;

@ConditionalOnProperty(value = "wp37-multitenancy-starter.enabled",
		havingValue = "true",
		matchIfMissing = true)
@SpringBootApplication
public class MultitenancyStarterConfiguration {

	@Bean("defaultLiquibaseProperties")
	@ConfigurationProperties(prefix = "wp37-multitenancy-starter.default-liquibase")
	public CustomLiquibaseProperties defaultLiquibaseProperties() {
		return new CustomLiquibaseProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "wp37-multitenancy-starter")
	public StarterConfigurationProperties starterConfigurationProperties() {
		return new StarterConfigurationProperties();
	}

}
