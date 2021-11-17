package org.wpstarters37.authorizationstarter.domain.services;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;

@Service
public class AlwaysTrueUserVerificationService
        implements UserVerificationService {

    @Override
    public void sendVerification(ClientDetails clientDetails) {
    }

    @Override
    public boolean verify(String ticket) {
        return true;
    }

    @Override
    public boolean check(ClientDetails clientDetails) {
        return true;
    }
}
