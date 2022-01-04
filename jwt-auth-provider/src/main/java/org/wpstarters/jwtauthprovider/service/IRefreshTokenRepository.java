package org.wpstarters.jwtauthprovider.service;

import org.springframework.data.repository.CrudRepository;
import org.wpstarters.jwtauthprovider.model.RefreshToken;

import java.util.UUID;

public interface IRefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {

}
