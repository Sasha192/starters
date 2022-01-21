package org.wpstarters.jwtauthprovider.service.impl;

import org.wpstarters.jwtauthprovider.dto.IAuthenticationRequest;
import org.wpstarters.jwtauthprovider.dto.SocialAccountInfo;
import org.wpstarters.jwtauthprovider.model.ProviderType;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.IUserVerificationService;

public class DummmyUserVerificationService implements IUserVerificationService {


    @Override
    public void sendVerificationForRequest(IAuthenticationRequest authencationRequest) throws ExtendedAuthenticationException {
    }

    @Override
    public boolean verifyCodeForRequest(IAuthenticationRequest authencationRequest) throws ExtendedAuthenticationException {
        return true;
    }

    @Override
    public SocialAccountInfo verifySocialAccount(String authorizationCode, ProviderType provider) {
        return null;
    }


}
