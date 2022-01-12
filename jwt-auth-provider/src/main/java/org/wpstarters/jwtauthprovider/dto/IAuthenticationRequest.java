package org.wpstarters.jwtauthprovider.dto;

import org.wpstarters.jwtauthprovider.model.ProviderType;

public interface IAuthenticationRequest {

    String getNonce();

    String getId();

    ProviderType getProvider();

    String getPassword();

    String getCode();

}
