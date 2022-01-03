package org.wpstarters.jwtauthprovider.service;

import com.nimbusds.jose.jwk.JWKSet;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyPair;

public interface IEncryptionKeys {

    KeyPair keyPair();

    JWKSet jwkSet();


}
