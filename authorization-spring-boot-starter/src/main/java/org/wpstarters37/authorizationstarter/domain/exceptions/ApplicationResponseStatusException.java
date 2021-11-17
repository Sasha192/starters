package org.wpstarters37.authorizationstarter.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApplicationResponseStatusException
        extends ResponseStatusException
        implements ApplicationException {

    public ApplicationResponseStatusException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }
}
