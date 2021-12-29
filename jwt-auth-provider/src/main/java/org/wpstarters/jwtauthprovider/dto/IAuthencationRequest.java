package org.wpstarters.jwtauthprovider.dto;

public interface IAuthencationRequest {

    String getNonce();

    String getId();

    ProviderType getProvider();

    String getPassword();

    String getCode();

}
