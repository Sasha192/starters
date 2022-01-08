package org.wpstarters.jwtauthprovider.service.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.wpstarters.jwtauthprovider.props.JksConfigurationProperties;
import org.wpstarters.jwtauthprovider.service.IEncryptionKeys;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

@Service
public class EncryptionKeys implements IEncryptionKeys {

    private KeyPair keyPair;
    private JWKSet jwkSet;

    public EncryptionKeys(JksConfigurationProperties jksConfigurationProperties) throws Exception {

        RSAPublicKey rsaPublicKey = readPublicKey(jksConfigurationProperties.getPublicKeyPath());
        RSAPrivateKey rsaPrivateKey = readPrivateKey(
                jksConfigurationProperties.getPrivateKeyPath(),
                jksConfigurationProperties.getPassword()
        );

        RSAKey.Builder builder = new RSAKey.Builder(rsaPublicKey)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(jksConfigurationProperties.getKeyId());

        this.jwkSet = new JWKSet(builder.build());
        this.keyPair = new KeyPair(rsaPublicKey, rsaPrivateKey );
    }

    private RSAPrivateKey readPrivateKey(String privateKeyPath, String password) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        KeyPair kp;
        try (InputStream io = new ClassPathResource(privateKeyPath).getInputStream();
             Reader reader = new InputStreamReader(io);
             PEMParser pemParser = new PEMParser(reader)) {

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            Object privateKeyObject = pemParser.readObject();

            if (privateKeyObject instanceof PEMEncryptedKeyPair) {
                // Encrypted key - we will use provided password
                PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) privateKeyObject;
                PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(password.toCharArray());
                kp = converter.getKeyPair(ckp.decryptKeyPair(decProv));
            } else {
                // Unencrypted key - no password needed
                PEMKeyPair ukp = (PEMKeyPair) privateKeyObject;
                kp = converter.getKeyPair(ukp);
            }

            return (RSAPrivateKey) kp.getPrivate();

        }
    }

    @Override
    public KeyPair keyPair() {
        return keyPair;
    }

    @Override
    public JWKSet jwkSet() {
        return jwkSet;
    }

    private RSAPublicKey readPublicKey(String publicKeyPath) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        try (InputStream io = new ClassPathResource(publicKeyPath).getInputStream();
             Reader reader = new InputStreamReader(io);
             PemReader pemReader = new PemReader(reader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
            return (RSAPublicKey) factory.generatePublic(pubKeySpec);
        }
    }
}
