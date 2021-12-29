package org.wpstarters.jwtauthprovider.service;

public interface IPublicKeyService
        extends IEncryptionService,
        IDecryptionService {


    /**
     * @return text in form of:
     *
     *-----BEGIN PUBLIC KEY-----
     * Base64 encoding of the DER encoded public key
     * -----END PUBLIC KEY-----
     *
     */
    String publicKey();

}
