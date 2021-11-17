package org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.SharedSchema;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.StarterConfigurationProperties;

@Configuration
@Conditional(SharedSchema.class)
public class SharedSchemaAutoConfiguration {

    private final StarterConfigurationProperties starterProperties;

    public SharedSchemaAutoConfiguration(StarterConfigurationProperties starterProperties) {
        this.starterProperties = starterProperties;
    }


}
