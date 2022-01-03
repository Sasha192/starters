package org.wpstarters.jwtauthprovider.api.state;

import org.wpstarters.jwtauthprovider.dto.IStateMessage;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;

public class StateMessage implements IStateMessage {

    private String message;
    private boolean success;
    private ExceptionState exceptionState;

    public StateMessage(String message, boolean success, ExceptionState exceptionState) {
        this.message = message;
        this.success = success;
        this.exceptionState = exceptionState;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ExceptionState getRequestState() {
        return exceptionState;
    }

    public void setRequestState(ExceptionState exceptionState) {
        this.exceptionState = exceptionState;
    }
}
