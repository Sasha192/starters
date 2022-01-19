package org.wpstarters.jwtauthprovider.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wpstarters.jwtauthprovider.config.context.HttpContextHolder;

import javax.validation.constraints.NotNull;
import java.util.List;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final List<HandlerInterceptor> interceptors;

    public WebConfiguration(List<HandlerInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public void addInterceptors(InterceptorRegistry registry) {

        for (HandlerInterceptor interceptor: interceptors) {
            registry.addInterceptor(interceptor);
        }

    }

}
