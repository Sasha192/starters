package org.wpstarters.jwtauthprovider.repository;

import org.springframework.data.repository.CrudRepository;
import org.wpstarters.jwtauthprovider.model.TokenHistory;

public interface ITokenHistoryRepository extends CrudRepository<TokenHistory, Long> {

}
