package org.wpstarters.jwtauthprovider.dto;

import org.wpstarters.jwtauthprovider.model.ProviderType;

public class LoginRequest implements IAuthenticationRequest {

    private String id;
    private ProviderType provider;
    private String code;
    private String password;
    private String nonce;

    public LoginRequest() {
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


    public static final class Builder {
        private String id;
        private ProviderType provider;
        private String code;
        private String password;
        private String nonce;

        public Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder provider(ProviderType provider) {
            this.provider = provider;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder nonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public LoginRequest build() {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setId(id);
            loginRequest.setProvider(provider);
            loginRequest.setCode(code);
            loginRequest.setPassword(password);
            loginRequest.setNonce(nonce);
            return loginRequest;
        }
    }
}
