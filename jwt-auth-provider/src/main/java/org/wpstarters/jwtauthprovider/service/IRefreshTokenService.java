package org.wpstarters.jwtauthprovider.service;

import org.wpstarters.jwtauthprovider.dto.RefreshToken;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;

public interface IRefreshTokenService {

    RefreshToken findOne(String tokenId);

    RefreshToken save(String username);

    RefreshToken save(RefreshToken token);

    RefreshToken findByUsername(String username);

    boolean isValid(RefreshToken refreshToken) throws ExtendedAuthenticationException;

}
