package org.wpstarters.jwtauthprovider.service.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Service;
import org.wpstarters.jwtauthprovider.dto.JksConfigurationProperties;
import org.wpstarters.jwtauthprovider.service.IKeyPairSupplier;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

@Service
public class KeyPairSupplier implements IKeyPairSupplier {

    private KeyPair keyPair;
    private JWKSet jwkSet;

    public KeyPairSupplier(JksConfigurationProperties jksConfigurationProperties) {
        ClassPathResource ksFile = new ClassPathResource(jksConfigurationProperties.getJksFilePath());
        KeyStoreKeyFactory ksFactory = new KeyStoreKeyFactory(ksFile, jksConfigurationProperties.getKeyPass().toCharArray());
        keyPair = ksFactory.getKeyPair(jksConfigurationProperties.getAlias());

        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair().getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(jksConfigurationProperties.getKeyId());
        jwkSet = new JWKSet(builder.build());
    }

    @Override
    public KeyPair keyPair() {
        return keyPair;
    }

    @Override
    public JWKSet jwkSet() {
        return jwkSet;
    }
}
