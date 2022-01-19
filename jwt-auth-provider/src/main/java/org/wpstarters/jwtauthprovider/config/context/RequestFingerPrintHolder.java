package org.wpstarters.jwtauthprovider.config.context;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.wpstarters.jwtauthprovider.throttle.IToFingerprintOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestFingerPrintHolder implements HandlerInterceptor {

    public static ThreadLocal<String> fingerPrint = new ThreadLocal<>();

    private final IToFingerprintOperator toFingerprintOperator;

    public RequestFingerPrintHolder(IToFingerprintOperator toFingerprintOperator) {
        this.toFingerprintOperator = toFingerprintOperator;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        fingerPrint.set(toFingerprintOperator.apply(req));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) {
        fingerPrint.remove();
    }
}