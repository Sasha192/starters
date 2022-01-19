package org.wpstarters.commontoolsstarter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wpstarters.commontoolsstarter.context.HttpContextHolder;

@Configuration
public class CommonStarterAutoConfiguration implements WebMvcConfigurer {

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HttpContextHolder());
    }

}
