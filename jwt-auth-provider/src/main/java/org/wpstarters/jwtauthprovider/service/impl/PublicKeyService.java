package org.wpstarters.jwtauthprovider.service.impl;

import org.springframework.stereotype.Service;
import org.wpstarters.jwtauthprovider.service.IEncryptionKeys;
import org.wpstarters.jwtauthprovider.service.IPublicKeyService;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

@Service
public class PublicKeyService implements IPublicKeyService {

    private static final String BEGIN_HEADER =
            "-----BEGIN PUBLIC KEY-----\n";

    private static final String END_HEADER =
            "\n-----END PUBLIC KEY-----";

    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private RSAPublicKey rsaPublicKey;
    private RSAPrivateKey rsaPrivateKey;

    public PublicKeyService(IEncryptionKeys keyPairSupplier)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        rsaPublicKey = (RSAPublicKey) keyPairSupplier.keyPair().getPublic();
        rsaPrivateKey = (RSAPrivateKey) keyPairSupplier.keyPair().getPrivate();

        encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);

        decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
    }

    @Override
    public String decrypt(String encryptedText) throws IllegalBlockSizeException, BadPaddingException {
        byte[] encBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decBytes = decryptCipher.doFinal(encBytes);
        return new String(decBytes, StandardCharsets.UTF_8);
    }

    @Override
    public String encrypt(String plainText) throws IllegalBlockSizeException, BadPaddingException {
        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytesMessage = encryptCipher.doFinal(plainBytes);
        return Base64.getEncoder().encodeToString(encryptedBytesMessage);
    }

    @Override
    public String publicKey() {
        String encodedPublicKey = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
        return BEGIN_HEADER + encodedPublicKey + END_HEADER;
    }
}
