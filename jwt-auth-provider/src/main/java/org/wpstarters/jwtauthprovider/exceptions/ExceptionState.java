package org.wpstarters.jwtauthprovider.exceptions;

public enum ExceptionState {

    EXPIRED_TOKEN, INVALID_TOKEN, USED_NONCE, EXPIRED_NONCE, INVALID_NONCE, EXPIRED_ACCOUNT, INVALID_ACCOUNT, INTERNAL_SERVER_ERROR, INVALID_VERIFICATION_CODE, EXPIRED_VERIFICATION_CODE, INVALID_REFRESH_TOKEN, NO_SUCH_USER, THROTTLED;

}
