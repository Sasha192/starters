package org.wpstarters37.authorizationstarter.configuration.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

public class UnsupportedUserDetailsService
        implements ICustomUserDetailsService<CustomUserDetails> {

    @Override
    public void save(CustomUserDetails userDetails) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        throw new UnsupportedOperationException();
    }
}
