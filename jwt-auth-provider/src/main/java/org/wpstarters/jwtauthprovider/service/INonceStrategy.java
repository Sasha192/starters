package org.wpstarters.jwtauthprovider.service;


public interface INonceStrategy {

    boolean validNonce(String nonce);

    String generateNonce();

    boolean saveNonce(String nonce);

}
