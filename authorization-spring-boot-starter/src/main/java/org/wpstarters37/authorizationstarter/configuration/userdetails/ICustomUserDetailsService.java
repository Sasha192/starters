package org.wpstarters37.authorizationstarter.configuration.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface ICustomUserDetailsService<T extends UserDetails> extends UserDetailsService {

    void save(T userDetails);

}
