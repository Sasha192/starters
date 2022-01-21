package org.wpstarters.jwtauthprovider.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.wpstarters.jwtauthprovider.dto.IAuthenticationRequest;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.AbstractSocialUserVerificationService;
import org.wpstarters.jwtauthprovider.service.utils.IMailService;

import javax.servlet.http.Cookie;
import java.util.concurrent.ThreadLocalRandom;

public class MailUserVerificationService extends AbstractSocialUserVerificationService {

    private static final String USER_VERIFICATION = "USER_VERIFICATION";

    private final IMailService mailService;
    private final PasswordEncoder oneWayFunction;

    public MailUserVerificationService(IMailService mailService) {
        this.mailService = mailService;
        this.oneWayFunction = new Pbkdf2PasswordEncoder();
    }

    @Override
    public void sendVerificationForRequest(IAuthenticationRequest authencationRequest) throws ExtendedAuthenticationException {

        String rawCode = generateCode();
        String email = authencationRequest.getId();

        mailService.sendCode(email, "Verification rawCode", rawCode);

        String oneWayCode = oneWayFunction.encode(rawCode);
        Cookie cookieNonce = new Cookie(USER_VERIFICATION, oneWayCode);
        CookieUtils.addSecureCookie(cookieNonce, true);
    }

    @Override
    public boolean verifyCodeForRequest(IAuthenticationRequest authencationRequest) throws ExtendedAuthenticationException {

        String rawCode = authencationRequest.getCode();
        Cookie userVerificationCookie = CookieUtils.retrieveCookie(USER_VERIFICATION);

        if (userVerificationCookie != null) {
            String oneWayCode = userVerificationCookie.getValue();
            return oneWayFunction.matches(rawCode, oneWayCode);
        }

        return false;
    }

    private String generateCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(1001, 99999));
    }
}
