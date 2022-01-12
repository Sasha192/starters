package org.wpstarters.jwtauthprovider.service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public interface IEncryptionService {

    String encrypt(String plainText) throws IllegalBlockSizeException, BadPaddingException;

}
