package org.wpstarters37.authorizationstarter.api;


import org.wpstarters37.authorizationstarter.domain.model.dto.ApplicationMessageDto;

public final class ApplicationStateResponse extends ApplicationMessageDto {

    private final boolean success;

    public ApplicationStateResponse(String message, boolean success) {
        super(message);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
