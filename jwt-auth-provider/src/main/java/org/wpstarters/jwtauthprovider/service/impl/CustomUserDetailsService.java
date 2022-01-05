package org.wpstarters.jwtauthprovider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wpstarters.jwtauthprovider.model.CustomUserDetails;
import org.wpstarters.jwtauthprovider.exceptions.UsernameAlreadyExists;
import org.wpstarters.jwtauthprovider.repository.UserDetailsRepository;
import org.wpstarters.jwtauthprovider.service.IUserDetailsService;

import java.util.Map;

public class CustomUserDetailsService implements IUserDetailsService {

    private final UserDetailsRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    public CustomUserDetailsService(UserDetailsRepository repository, JdbcTemplate jdbcTemplate, PasswordEncoder encoder, ObjectMapper objectMapper) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @Override
    public CustomUserDetails save(CustomUserDetails customUserDetails) throws UsernameAlreadyExists {

        return repository.save(customUserDetails);

    }

    @Override
    public void updatePassword(String username, String password) {

        String encodedPassword = encoder.encode(password);
        String query = String.format("UPDATE user_details ud SET ud.password = %s WHERE ud.username = %s", encodedPassword, username);
        jdbcTemplate.update(query);


    }

    @Override
    public void updateDetails(String username, Map<String, Object> details) throws JsonProcessingException {

        String extraDetails = objectMapper.writeValueAsString(details);
        String query = String.format("UPDATE user_details ud SET ud.extra_details = %s WHERE ud.username = %s", extraDetails, username);
        jdbcTemplate.update(query);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
