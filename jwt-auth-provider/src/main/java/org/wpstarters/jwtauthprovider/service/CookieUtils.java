package org.wpstarters.jwtauthprovider.service;

import org.apache.tomcat.util.http.CookieProcessorBase;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.wpstarters.jwtauthprovider.config.HttpContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static final String SET_COOKIE_HEADER = "Set-Cookie";

    private static final CookieProcessorBase cookieProcessor = new Rfc6265CookieProcessor();

    public static void addCookie(Cookie cookieNonce, boolean strict) {
        HttpServletResponse response = HttpContextHolder.response.get();
        HttpServletRequest request = HttpContextHolder.request.get();
        if (strict) {
            cookieProcessor.setSameSiteCookies("Strict");
        }
        String headerValue = cookieProcessor.generateHeader(cookieNonce, request);
        response.addHeader(SET_COOKIE_HEADER, headerValue);
    }
}
