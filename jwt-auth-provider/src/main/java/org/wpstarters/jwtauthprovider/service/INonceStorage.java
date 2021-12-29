package org.wpstarters.jwtauthprovider.service;

import org.springframework.data.repository.CrudRepository;
import org.wpstarters.jwtauthprovider.dto.PersistenceNonce;

import java.util.UUID;

public interface INonceStorage extends CrudRepository<PersistenceNonce, UUID> {

}
