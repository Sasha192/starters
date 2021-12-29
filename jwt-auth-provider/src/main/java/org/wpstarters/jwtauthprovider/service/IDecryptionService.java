package org.wpstarters.jwtauthprovider.service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public interface IDecryptionService {

    String decrypt(String encryptedText) throws IllegalBlockSizeException, BadPaddingException;

}
