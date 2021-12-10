package org.wpstarters.multitenancyspringbootstarter.multitenancy.exceptions;

public class TenantCreationException extends RuntimeException {
    public TenantCreationException(String message, Throwable e) {
        super(message, e);
    }
}
