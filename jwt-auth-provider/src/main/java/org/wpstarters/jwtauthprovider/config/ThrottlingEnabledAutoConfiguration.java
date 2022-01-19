package org.wpstarters.jwtauthprovider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wpstarters.jwtauthprovider.config.conditional.ThrottlingEnabled;
import org.wpstarters.jwtauthprovider.config.context.RequestFingerPrintHolder;
import org.wpstarters.jwtauthprovider.props.ThrottlingConfigurationProperties;
import org.wpstarters.jwtauthprovider.throttle.ConcurrentHashMapThrottling;
import org.wpstarters.jwtauthprovider.throttle.IThrottleService;
import org.wpstarters.jwtauthprovider.throttle.IToFingerprintOperator;
import org.wpstarters.jwtauthprovider.throttle.IpFingerprintOperator;
import org.wpstarters.jwtauthprovider.throttle.ThrottleFilterAspect;


@Configuration
@Conditional(value = ThrottlingEnabled.class)
public class ThrottlingEnabledAutoConfiguration implements WebMvcConfigurer {

    private final IThrottleService throttleService;
    private final IToFingerprintOperator fingerPrintOperator;

    public ThrottlingEnabledAutoConfiguration(IThrottleService throttleService, ThrottlingConfigurationProperties throttlingConfigurationProperties) {
        this.throttleService = throttleService;
        this.fingerPrintOperator = toFingerprintOperator(throttlingConfigurationProperties.isConsiderXForward());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestFingerPrintHolder(fingerPrintOperator));
    }

    @Bean
    public ThrottleFilterAspect throttleFilterAspect() {
        return new ThrottleFilterAspect(throttleService);
    }

    @Bean
    public IThrottleService throttleService(ThrottlingConfigurationProperties throttlingConfigurationProperties) {

        return new ConcurrentHashMapThrottling(throttlingConfigurationProperties.getTimeWindowInSecs(), throttlingConfigurationProperties.getMaxNumberOfRequests());

    }

    private IToFingerprintOperator toFingerprintOperator(boolean considerXForward) {

        return new IpFingerprintOperator(considerXForward);

    }

}
