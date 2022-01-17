package org.wpstarters.jwtauthprovider.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Component;
import org.wpstarters.commontoolsstarter.context.HttpContextHolder;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.INonceStrategy;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

import static org.wpstarters.jwtauthprovider.service.impl.CookieUtils.SET_COOKIE_HEADER;

@Component
public class CookieNonceStrategy implements INonceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CookieNonceStrategy.class);
    private static final String COOKIE_NONCE = "COOKIE_NONCE";

    private final PasswordEncoder passwordEncoder;

    public CookieNonceStrategy() {
        this.passwordEncoder = new Pbkdf2PasswordEncoder(String.valueOf(new SecureRandom().nextLong()));
    }

    @Override
    public boolean validNonce(String nonce) {
        if (nonce == null) {
            return false;
        }
        try {
            if (HttpContextHolder.request.get() != null && HttpContextHolder.response.get() != null) {
                HttpServletRequest request = HttpContextHolder.request.get();

                Cookie cookieNonce = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(COOKIE_NONCE))
                        .findAny().orElseThrow(
                                () -> new ExtendedAuthenticationException("Cookie " + COOKIE_NONCE + " is empty", ExceptionState.INVALID_NONCE)
                        );

                if (cookieNonce.getSecure() && cookieNonce.isHttpOnly()) {
                    return passwordEncoder.matches(cookieNonce.getValue(), nonce);
                } else {
                    throw new ExtendedAuthenticationException("Invalid cookie: must be httpOnly and secured", ExceptionState.INVALID_NONCE);
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
        }
        throw new ExtendedAuthenticationException("Error occurred", ExceptionState.INTERNAL_SERVER_ERROR);
    }

    @Override
    public String generateNonce() {
        try {
            String nonce = UUID.randomUUID().toString().replaceAll("-", "");
            Cookie cookieNonce = new Cookie(COOKIE_NONCE, nonce);
            CookieUtils.addSecureCookie(cookieNonce, true);
            return passwordEncoder.encode(nonce);
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting the nonce", e);
            throw new ExtendedAuthenticationException("", ExceptionState.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean saveNonce(String encryptedNonce) {
        if (HttpContextHolder.response.get() != null) {
            HttpServletResponse response = HttpContextHolder.response.get();
            String headerValue = response.getHeader(SET_COOKIE_HEADER);
            return headerValue != null &&
                    headerValue.contains("SameSite=Strict") &&
                    headerValue.contains(COOKIE_NONCE) &&
                    headerValue.contains("HttpOnly");

        }
        return false;
    }
}
