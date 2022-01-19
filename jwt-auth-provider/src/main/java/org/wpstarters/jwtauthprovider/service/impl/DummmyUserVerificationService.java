package org.wpstarters.jwtauthprovider.service.impl;

import org.springframework.stereotype.Service;
import org.wpstarters.jwtauthprovider.dto.IAuthenticationRequest;
import org.wpstarters.jwtauthprovider.model.ProviderType;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.service.IUserVerificationService;

import java.util.Map;

@Service
public class DummmyUserVerificationService implements IUserVerificationService {


    @Override
    public boolean sendVerificationForRequest(IAuthenticationRequest authencationRequest) throws ExtendedAuthenticationException {
        return true;
    }

    @Override
    public boolean verifyCodeForRequest(IAuthenticationRequest authencationRequest) throws ExtendedAuthenticationException {
        return true;
    }

    @Override
    public Map<String, Object> verifySocialAccount(String authorizationCode, ProviderType provider) {
        return null;
    }


}
