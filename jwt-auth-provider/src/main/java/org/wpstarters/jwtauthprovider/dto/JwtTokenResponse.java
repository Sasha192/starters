package org.wpstarters.jwtauthprovider.dto;

public class JwtTokenResponse {

    private final String jwtIdentityToken;

    private final String refreshToken;


    public JwtTokenResponse(String jwtIdentityToken, String refreshToken) {
        this.jwtIdentityToken = jwtIdentityToken;
        this.refreshToken = refreshToken;
    }

    public String getJwtIdentityToken() {
        return jwtIdentityToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
