package org.wpstarters.jwtauthprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.model.CustomUserDetails;

public interface ITokenService {
    String generateJwtToken(CustomUserDetails userDetails)
            throws JsonProcessingException;

    String refreshToken(String jwtToken) throws JsonProcessingException;

    String getUserNameFromJwtToken(String token);

    boolean validateJwtToken(String authToken) throws ExtendedAuthenticationException;
}
