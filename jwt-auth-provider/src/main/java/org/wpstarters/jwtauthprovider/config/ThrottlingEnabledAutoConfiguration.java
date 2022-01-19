package org.wpstarters.jwtauthprovider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wpstarters.jwtauthprovider.config.conditional.ThrottlingEnabled;
import org.wpstarters.jwtauthprovider.config.context.RequestFingerprintUtil;
import org.wpstarters.jwtauthprovider.props.ThrottlingConfigurationProperties;
import org.wpstarters.jwtauthprovider.throttle.ConcurrentHashMapThrottling;
import org.wpstarters.jwtauthprovider.throttle.IThrottleService;
import org.wpstarters.jwtauthprovider.throttle.ThrottleFilterAspect;


@Configuration
@Conditional(value = ThrottlingEnabled.class)
public class ThrottlingEnabledAutoConfiguration {

    private final IThrottleService throttleService;
    private final ThrottlingConfigurationProperties throttlingConfigurationProperties;

    public ThrottlingEnabledAutoConfiguration(IThrottleService throttleService, ThrottlingConfigurationProperties throttlingConfigurationProperties) {
        this.throttleService = throttleService;
        this.throttlingConfigurationProperties = throttlingConfigurationProperties;
    }

    @Bean
    public HandlerInterceptor requestFingerPrintInterceptor() {
        return new RequestFingerprintUtil(throttlingConfigurationProperties.isConsiderXForward());
    }

    @Bean
    public ThrottleFilterAspect throttleFilterAspect() {
        return new ThrottleFilterAspect(throttleService);
    }

    @Bean
    public IThrottleService throttleService(ThrottlingConfigurationProperties throttlingConfigurationProperties) {

        return new ConcurrentHashMapThrottling(throttlingConfigurationProperties.getTimeWindowInSecs(), throttlingConfigurationProperties.getMaxNumberOfRequests());

    }

}
