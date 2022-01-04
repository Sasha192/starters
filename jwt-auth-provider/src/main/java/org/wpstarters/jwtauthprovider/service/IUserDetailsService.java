package org.wpstarters.jwtauthprovider.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.wpstarters.jwtauthprovider.model.CustomUserDetails;
import org.wpstarters.jwtauthprovider.exceptions.UsernameAlreadyExists;

import java.util.Map;

public interface IUserDetailsService extends UserDetailsService {

    CustomUserDetails save(CustomUserDetails customUserDetails) throws UsernameAlreadyExists;

    void updatePassword(String username, String password);

    void updateDetails(String username, Map<String, Object> details) throws JsonProcessingException;


}
