package org.wpstarters.jwtauthprovider.dto;

import org.wpstarters.jwtauthprovider.model.ProviderType;

import java.util.Map;

public class SignUpRequest implements IAuthenticationRequest {

    private String id;
    private ProviderType provider;
    private String code;
    private String password;
    private String nonce;
    private Map<String, Object> publicDetails;

    public SignUpRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public Map<String, Object> getPublicDetails() {
        return publicDetails;
    }

    public void setPublicDetails(Map<String, Object> publicDetails) {
        this.publicDetails = publicDetails;
    }
}
