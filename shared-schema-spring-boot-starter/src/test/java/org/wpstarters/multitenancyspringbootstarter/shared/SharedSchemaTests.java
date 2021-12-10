package org.wpstarters.multitenancyspringbootstarter.shared;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.wpstarters.multitenancyspringbootstarter.multitenancy.sharedschema.SharedSchemaAutoConfiguration;

@Configuration
@Import(value = {
        MultitenancyStarterConfiguration.class,
        SharedSchemaAutoConfiguration.class,
        YamlEnvironmentBeanProcessor.class
})
@Conditional(SharedSchema.class)
public class SharedSchemaTests {
}
