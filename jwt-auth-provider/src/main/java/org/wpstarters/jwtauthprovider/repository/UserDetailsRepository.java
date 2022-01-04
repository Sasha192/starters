package org.wpstarters.jwtauthprovider.repository;

import org.springframework.data.repository.CrudRepository;
import org.wpstarters.jwtauthprovider.model.CustomUserDetails;

public interface UserDetailsRepository extends CrudRepository<CustomUserDetails, String> {


}
