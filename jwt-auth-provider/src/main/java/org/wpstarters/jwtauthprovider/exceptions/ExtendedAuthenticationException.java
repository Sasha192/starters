package org.wpstarters.jwtauthprovider.exceptions;

import org.springframework.security.core.AuthenticationException;


public class ExtendedAuthenticationException extends AuthenticationException {

    private final ExceptionState exceptionState;

    public ExtendedAuthenticationException(String msg, ExceptionState exceptionState) {
        super(msg);
        this.exceptionState = exceptionState;
    }

    public ExceptionState getExceptionState() {
        return exceptionState;
    }
}
