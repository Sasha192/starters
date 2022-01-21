package org.wpstarters.jwtauthprovider.service;

import org.wpstarters.jwtauthprovider.dto.IAuthenticationRequest;
import org.wpstarters.jwtauthprovider.dto.SocialAccountInfo;
import org.wpstarters.jwtauthprovider.model.ProviderType;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;

import javax.validation.constraints.NotNull;

// TODO: implement throttling on sendVerificationRequest!, verifySocialAccount! and others
public interface IUserVerificationService {

    void sendVerificationForRequest(IAuthenticationRequest authencationRequest)
            throws ExtendedAuthenticationException;

    // TODO: check nonce, check throttling
    boolean verifyCodeForRequest(IAuthenticationRequest authencationRequest)
            throws ExtendedAuthenticationException;

    SocialAccountInfo verifySocialAccount(@NotNull String authorizationCode, @NotNull ProviderType provider);

}
