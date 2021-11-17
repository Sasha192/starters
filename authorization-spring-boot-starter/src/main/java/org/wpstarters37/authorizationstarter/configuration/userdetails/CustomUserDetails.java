package org.wpstarters37.authorizationstarter.configuration.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Access(value=AccessType.PROPERTY)
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String groups;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;
    private Set<GrantedAuthority> authorities;


    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities == null) {
            authorities = new HashSet<>();
            authorities = Arrays.stream(
                    groups.replaceAll("\\s", "").split(",")
            ).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        }
        return authorities;
    }

    @Override
    @Id
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    @Override
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    @Override
    @Column(name = "account_non_expired")
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    @Column(name = "account_non_locked")
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    @Column(name = "credentials_non_expired")
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Column(name = "groups")
    public String getGroups() {
        return groups;
    }

    @Override
    @Column(name = "enabled")
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public static final class Builder {
        private String username;
        private String password;
        private String groups;
        private boolean accountNonExpired = true;
        private boolean accountNonLocked = true;
        private boolean credentialsNonExpired = true;
        private boolean enabled = true;
        private Set<GrantedAuthority> authorities;

        private Builder() {
        }

        public static Builder aCustomUserDetails() {
            return new Builder();
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withGroups(String groups) {
            this.groups = groups;
            return this;
        }

        public Builder withAccountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public Builder withAccountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public Builder withCredentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public Builder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder withAuthorities(Set<GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public CustomUserDetails build() {
            CustomUserDetails customUserDetails = new CustomUserDetails();
            customUserDetails.setUsername(username);
            customUserDetails.setPassword(password);
            customUserDetails.setGroups(groups);
            customUserDetails.setAccountNonExpired(accountNonExpired);
            customUserDetails.setAccountNonLocked(accountNonLocked);
            customUserDetails.setCredentialsNonExpired(credentialsNonExpired);
            customUserDetails.setEnabled(enabled);
            customUserDetails.authorities = this.authorities;
            return customUserDetails;
        }
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", groups='" + groups + '\'' +
                '}';
    }
}
