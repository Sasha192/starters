package org.wpstarters.jwtauthprovider.config.context;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestFingerprintUtil implements HandlerInterceptor {

    private static final ThreadLocal<String> ipAddress = new ThreadLocal<>();
    private static final ThreadLocal<String> userAgent = new ThreadLocal<>();

    private final boolean considerXHeaders;

    public RequestFingerprintUtil(boolean considerXHeaders) {
        this.considerXHeaders = considerXHeaders;
    }

    public static String requestIpAddress() {
        return ipAddress.get();
    }

    public static String requestUserAgent() {
        return userAgent.get();
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        ipAddress.set(retrieveRequestFingerprint(req));
        userAgent.set(retrieveUserAgent(req));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView modelAndView) {
        ipAddress.remove();
        userAgent.remove();
    }

    private String retrieveRequestFingerprint(HttpServletRequest request) {

        String xHeaderValue = retrieveXHeaderValue(request);
        if (considerXHeaders && xHeaderValue != null) {
            return xHeaderValue;
        }
        return request.getRemoteAddr();

    }

    private String retrieveUserAgent(HttpServletRequest request) {

        if (request.getHeader("User-Agent") != null) {

            return request.getHeader("User-Agent");

        }

        return "";

    }

    private String retrieveXHeaderValue(HttpServletRequest request) {

        if (request.getHeader("X-Forwarded-For") != null) {
            return request.getHeader("X-Forwarded-For");
        } else if (request.getHeader("x-forwarded-for") != null) {
            return request.getHeader("x-forwarded-for");
        } else {
            return null;
        }

    }
}
