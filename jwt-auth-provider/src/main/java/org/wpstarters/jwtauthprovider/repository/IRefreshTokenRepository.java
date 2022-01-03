package org.wpstarters.jwtauthprovider.repository;

import org.springframework.data.repository.CrudRepository;
import org.wpstarters.jwtauthprovider.dto.RefreshToken;

import java.util.UUID;

public interface IRefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
}
