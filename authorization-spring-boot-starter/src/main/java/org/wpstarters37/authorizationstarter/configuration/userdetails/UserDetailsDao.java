package org.wpstarters37.authorizationstarter.configuration.userdetails;

import org.springframework.data.repository.CrudRepository;

public interface UserDetailsDao extends CrudRepository<CustomUserDetails, String> {
}
