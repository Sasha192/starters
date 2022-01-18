package org.wpstarters.jwtauthprovider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wpstarters.commontoolsstarter.ThrottlingEnabled;
import org.wpstarters.commontoolsstarter.context.FingerPrintHolder;
import org.wpstarters.commontoolsstarter.throttle.IThrottleService;
import org.wpstarters.commontoolsstarter.throttle.IToFingerprintOperator;
import org.wpstarters.commontoolsstarter.throttle.ThrottleFilterAspect;

@Configuration
@Conditional(value = ThrottlingEnabled.class)
public class ThrottlingEnabledAutoConfiguration implements WebMvcConfigurer {

    private final IThrottleService throttleService;
    private final IToFingerprintOperator fingerPrintOperator;

    public ThrottlingEnabledAutoConfiguration(IThrottleService throttleService, IToFingerprintOperator fingerprintOperator) {
        this.throttleService = throttleService;
        this.fingerPrintOperator = fingerprintOperator;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FingerPrintHolder(fingerPrintOperator));
    }

    @Bean
    public ThrottleFilterAspect throttleFilterAspect() {
        return new ThrottleFilterAspect(throttleService);
    }

}
