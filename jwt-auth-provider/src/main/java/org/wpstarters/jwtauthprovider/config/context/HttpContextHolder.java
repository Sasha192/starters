package org.wpstarters.jwtauthprovider.config.context;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpContextHolder implements HandlerInterceptor {

    public static ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();
    public static ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        request.set(req);
        response.set(res);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) {
        request.remove();
        response.remove();
    }
}
