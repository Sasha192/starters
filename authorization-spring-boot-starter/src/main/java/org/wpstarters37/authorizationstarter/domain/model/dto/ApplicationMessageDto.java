package org.wpstarters37.authorizationstarter.domain.model.dto;

public class ApplicationMessageDto implements AbstractDto {

    private String message;

    public ApplicationMessageDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
