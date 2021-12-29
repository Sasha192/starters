//package org.wpstarters.jwtauthprovider.service.impl;
//
//import org.springframework.boot.configurationprocessor.json.JSONException;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
//import org.springframework.stereotype.Service;
//import org.wpstarters.jwtauthprovider.dto.PersistenceNonce;
//import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
//import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
//import org.wpstarters.jwtauthprovider.service.INonceStrategy;
//import org.wpstarters.jwtauthprovider.service.INonceStorage;
//import org.wpstarters.jwtauthprovider.service.IPublicKeyService;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.IllegalBlockSizeException;
//import java.util.UUID;
//
//@Service
//public class PersistenceNonceStrategy implements INonceStrategy {
//
//    private final IPublicKeyService publicKeyService;
//    private final INonceStorage nonceStorage;
//
//    public NonceStrategy(IPublicKeyService publicKeyService, INonceStorage nonceStorage) {
//        this.publicKeyService = publicKeyService;
//        this.nonceStorage = nonceStorage;
//    }
//
//
//    @Override
//    public boolean isNotExpiredNonce(String encryptedRequestToken)
//            throws ExtendedAuthenticationException, IllegalBlockSizeException, BadPaddingException, JSONException {
//
//        String json = publicKeyService.decrypt(encryptedRequestToken);
//        JSONObject jsonObject = new JSONObject(json);
//
//        String nonce = jsonObject.getString("nonce");
//
//        PersistenceNonce persistenceNonce = nonceStorage.findById(UUID.fromString(nonce)).orElseThrow(
//                () -> new ExtendedAuthenticationException("No such nonce", ExceptionState.INVALID_NONCE)
//        );
//
//        if (persistenceNonce.getEncryptedRequest().equals(encryptedRequestToken)) {
//
//            if (persistenceNonce.isUsed()) {
//
//                nonceStorage.deleteById(persistenceNonce.getNonce());
//                throw new ExtendedAuthenticationException("Nonce has been already used", ExceptionState.USED_NONCE);
//
//            } else if (System.currentTimeMillis() <= persistenceNonce.getExpired()) {
//
//                persistenceNonce.setUsed(true);
//                nonceStorage.save(persistenceNonce);
//                return true;
//
//            }
//
//        }
//
//        throw new ExtendedAuthenticationException("Nonce is not valid", ExceptionState.INVALID_NONCE);
//
//    }
//
//    @Override
//    public boolean saveNonce(String encryptedNonce) {
//
//        String nonce = publicKeyService.decrypt(encryptedNonce);
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("requestUri", requestUri);
//        jsonObject.put("body", body);
//        jsonObject.put("nonce", nonce);
//
//        String encryptedRequest = publicKeyService.encrypt(jsonObject.toString());
//
//
//        PersistenceNonce persistenceNonce = new PersistenceNonce.Builder()
//                .nonce(UUID.fromString(nonce))
//                .encryptedRequest(encryptedRequest)
//                .expired(System.currentTimeMillis() + ScheduledNonceCleanupConfiguration.NONCE_TTL)
//                .build();
//
//        nonceStorage.save(persistenceNonce);
//
//    }
//}
