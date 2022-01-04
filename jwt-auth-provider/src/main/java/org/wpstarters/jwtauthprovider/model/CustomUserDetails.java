package org.wpstarters.jwtauthprovider.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "user_details")
public class CustomUserDetails implements UserDetails {

    private static final Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_USER"));

    @Id
    @Column(name = "username")
    private String username;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;
    private boolean basicAccount;
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;
    @Column(name = "extra_details", columnDefinition = "jsonb")
    // json as a string
    private String extraDetails;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isBasicAccount() {
        return basicAccount;
    }

    public void setBasicAccount(boolean basicAccount) {
        this.basicAccount = basicAccount;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public String getExtraDetails() {
        // json as a string
        return extraDetails;
    }

    public void setExtraDetails(String extraDetails) {
        this.extraDetails = extraDetails;
    }

    public static final class Builder {
        private String username;
        private String password;
        private boolean accountNonExpired = true;
        private boolean accountNonLocked = true;
        private boolean credentialsNonExpired = true;
        private boolean enabled = true;
        private boolean basicAccount = true;
        private ProviderType providerType = ProviderType.BASIC;
        private String extraDetails = "{}";

        private PasswordEncoder encoder;

        public Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder accountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public Builder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public Builder credentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder basicAccount(boolean basicAccount) {
            this.basicAccount = basicAccount;
            return this;
        }

        public Builder providerType(ProviderType providerType) {
            this.providerType = providerType;
            return this;
        }

        public Builder extraDetails(String extraDetails) {
            this.extraDetails = extraDetails;
            return this;
        }

        public CustomUserDetails build() {
            CustomUserDetails customUserDetails = new CustomUserDetails();
            customUserDetails.setBasicAccount(basicAccount);
            customUserDetails.setProviderType(providerType);
            customUserDetails.setExtraDetails(extraDetails);
            customUserDetails.password = encoder.encode(this.password);
            customUserDetails.enabled = this.enabled;
            customUserDetails.accountNonExpired = this.accountNonExpired;
            customUserDetails.username = this.username;
            customUserDetails.accountNonLocked = this.accountNonLocked;
            customUserDetails.credentialsNonExpired = this.credentialsNonExpired;
            return customUserDetails;
        }
    }
}
