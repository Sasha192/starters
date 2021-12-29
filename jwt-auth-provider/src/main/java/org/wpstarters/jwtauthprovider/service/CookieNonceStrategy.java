package org.wpstarters.jwtauthprovider.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wpstarters.jwtauthprovider.config.HttpContextHolder;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;

import static org.wpstarters.jwtauthprovider.service.CookieUtils.SET_COOKIE_HEADER;

@Component
public class CookieNonceStrategy implements INonceStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CookieNonceStrategy.class);
    private static final String COOKIE_NONCE = "COOKIE_NONCE";

    private final IEncryptionService encryptionService;
    private final String domain;

    public CookieNonceStrategy(IEncryptionService encryptionService,
                               @Value("${application.domain}") String domain) {
        this.encryptionService = encryptionService;
        this.domain = domain;
    }

    @Override
    public boolean validNonce(String nonce) {
        if (nonce == null) {
            return false;
        }
        try {
            if (HttpContextHolder.request.get() != null) {
                HttpServletRequest request = HttpContextHolder.request.get();

                Cookie cookieNonce = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals(COOKIE_NONCE))
                        .findAny().orElseThrow(
                                () -> new ExtendedAuthenticationException("Cookie " + COOKIE_NONCE + " is empty", ExceptionState.INVALID_NONCE)
                        );

                if (cookieNonce.getSecure() && cookieNonce.isHttpOnly()) {
                    String expectedNonce = encryptionService.encrypt(cookieNonce.getValue());
                    String actualNonce = nonce;
                    return expectedNonce.equals(actualNonce);
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
            if (HttpContextHolder.response.get() != null && HttpContextHolder.request.get() != null) {
                Cookie cookieNonce = new Cookie(COOKIE_NONCE, nonce);
                cookieNonce.setHttpOnly(true);
                cookieNonce.setSecure(true);
                cookieNonce.setMaxAge(60);
                cookieNonce.setPath("/");
                cookieNonce.setDomain(domain);

                CookieUtils.addCookie(cookieNonce, true);
            } else {
                throw new ExtendedAuthenticationException("Request and Response are null", ExceptionState.INTERNAL_SERVER_ERROR);
            }
            return encryptionService.encrypt(nonce);
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
