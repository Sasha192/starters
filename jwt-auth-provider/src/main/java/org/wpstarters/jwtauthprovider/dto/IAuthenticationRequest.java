package org.wpstarters.jwtauthprovider.dto;

public interface IAuthenticationRequest {

    String getNonce();

    String getId();

    ProviderType getProvider();

    String getPassword();

    String getCode();

}
