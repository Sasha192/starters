package org.wpstarters.jwtauthprovider.service;

import com.nimbusds.jose.jwk.JWKSet;

import java.security.KeyPair;

public interface IKeyPairSupplier {

    KeyPair keyPair();

    JWKSet jwkSet();

}
