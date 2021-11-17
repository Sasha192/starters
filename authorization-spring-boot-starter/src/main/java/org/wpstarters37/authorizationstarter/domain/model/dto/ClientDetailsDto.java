package org.wpstarters37.authorizationstarter.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class ClientDetailsDto {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("aud")
    private Set<String> resourceIds;
    @JsonProperty("scope")
    private Set<String> scope;
    @JsonProperty("authorities")
    private Set<String> authorities;
    private boolean active;
    @JsonProperty(value = "exp")
    private int expiration;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}
