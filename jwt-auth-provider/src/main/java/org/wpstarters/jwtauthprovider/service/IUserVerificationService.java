package org.wpstarters.jwtauthprovider.service;

import org.wpstarters.jwtauthprovider.dto.IAuthencationRequest;
import org.wpstarters.jwtauthprovider.dto.ProviderType;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;

import java.util.Map;

// TODO: implement throttling on sendVerificationRequest!, verifySocialAccount! and others
public interface IUserVerificationService {

    boolean sendVerificationForRequest(IAuthencationRequest authencationRequest)
            throws ExtendedAuthenticationException;

    // TODO: check nonce, check throttling
    boolean verifyCodeForRequest(IAuthencationRequest authencationRequest)
            throws ExtendedAuthenticationException;

    Map<String, Object> verifySocialAccount(String authorizationCode, ProviderType provider);

}
