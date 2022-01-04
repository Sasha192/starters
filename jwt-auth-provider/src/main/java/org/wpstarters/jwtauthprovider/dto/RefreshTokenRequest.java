package org.wpstarters.jwtauthprovider.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RefreshTokenRequest {

    private final String jwtToken;
    private final String nonce;

    @JsonCreator
    public RefreshTokenRequest(@JsonProperty String jwtToken,
                               @JsonProperty String nonce) {
        this.jwtToken = jwtToken;
        this.nonce = nonce;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getNonce() {
        return nonce;
    }
}
