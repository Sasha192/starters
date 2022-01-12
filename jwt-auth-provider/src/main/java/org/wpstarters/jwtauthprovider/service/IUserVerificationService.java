package org.wpstarters.jwtauthprovider.service;

import org.wpstarters.jwtauthprovider.dto.IAuthenticationRequest;
import org.wpstarters.jwtauthprovider.model.ProviderType;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;

import java.util.Map;

// TODO: implement throttling on sendVerificationRequest!, verifySocialAccount! and others
public interface IUserVerificationService {

    boolean sendVerificationForRequest(IAuthenticationRequest authencationRequest)
            throws ExtendedAuthenticationException;

    // TODO: check nonce, check throttling
    boolean verifyCodeForRequest(IAuthenticationRequest authencationRequest)
            throws ExtendedAuthenticationException;

    Map<String, Object> verifySocialAccount(String authorizationCode, ProviderType provider);

}
