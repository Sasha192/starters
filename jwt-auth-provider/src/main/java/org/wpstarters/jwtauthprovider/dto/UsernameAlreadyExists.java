package org.wpstarters.jwtauthprovider.dto;

import org.springframework.security.core.AuthenticationException;

public class UsernameAlreadyExists extends AuthenticationException {

    public UsernameAlreadyExists(String username) {
        super(String.format("Username %s already exists", username));
    }
}
