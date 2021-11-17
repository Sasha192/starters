package org.wpstarters.multitenancyspringbootstarter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;

@ConditionalOnProperty(value = "wp37-multitenancy-starter.enabled",
		havingValue = "true",
		matchIfMissing = true)
public class MultitenancyStarterConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "wp37-multitenancy-starter")
	public StarterConfigurationProperties starterConfigurationProperties() {
		return new StarterConfigurationProperties();
	}

}
