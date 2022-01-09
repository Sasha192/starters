package org.wpstarters.jwtauthprovider.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import org.wpstarters.jwtauthprovider.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface IRefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findRefreshTokenByUsernameEquals(String username);

    @Transactional
    void deleteRefreshTokenByUsernameEquals(String username);

}
