package org.wpstarters.jwtauthprovider.service.impl;

import org.apache.tomcat.util.http.CookieProcessorBase;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.web.util.WebUtils;
import org.wpstarters.commontoolsstarter.context.HttpContextHolder;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static final String SET_COOKIE_HEADER = "Set-Cookie";

    private static final CookieProcessorBase cookieProcessor = new Rfc6265CookieProcessor();

    public static void addCookie(Cookie cookieNonce, boolean strict) {
        if (HttpContextHolder.response.get() != null && HttpContextHolder.request.get() != null) {
            HttpServletResponse response = HttpContextHolder.response.get();
            HttpServletRequest request = HttpContextHolder.request.get();
            if (strict) {
                cookieProcessor.setSameSiteCookies("Strict");
            }
            String headerValue = cookieProcessor.generateHeader(cookieNonce, request);
            response.addHeader(SET_COOKIE_HEADER, headerValue);
        } else {
            throw new ExtendedAuthenticationException("Request and Response are null", ExceptionState.INTERNAL_SERVER_ERROR);
        }
    }

    public static void addSecureCookie(Cookie cookieNonce, boolean strict) {
        if (HttpContextHolder.response.get() != null && HttpContextHolder.request.get() != null) {
            HttpServletResponse response = HttpContextHolder.response.get();
            HttpServletRequest request = HttpContextHolder.request.get();

            cookieNonce.setHttpOnly(true);
            cookieNonce.setSecure(true);
            cookieNonce.setMaxAge(30);
            cookieNonce.setPath("/");

            if (strict) {
                cookieProcessor.setSameSiteCookies("Strict");
            }

            String headerValue = cookieProcessor.generateHeader(cookieNonce, request);
            response.addHeader(SET_COOKIE_HEADER, headerValue);
        } else {
            throw new ExtendedAuthenticationException("Request and Response are null", ExceptionState.INTERNAL_SERVER_ERROR);
        }
    }

    public static Cookie retrieveCookie(String cookieName) {
        if (HttpContextHolder.response.get() != null && HttpContextHolder.request.get() != null) {
            HttpServletRequest request = HttpContextHolder.request.get();
            return WebUtils.getCookie(request, cookieName);

        } else {
            throw new ExtendedAuthenticationException("Request and Response are null", ExceptionState.INTERNAL_SERVER_ERROR);
        }
    }
}
