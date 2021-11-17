package org.wpstarters37.authorizationstarter.domain.services;

import org.springframework.security.oauth2.provider.ClientDetails;

public interface UserVerificationService {

    void sendVerification(ClientDetails clientDetails);

    boolean verify(String ticket);

    boolean check(ClientDetails clientDetails);

}
