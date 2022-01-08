package org.wpstarters.jwtauthprovider.api.state;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.wpstarters.jwtauthprovider.dto.IStateMessage;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;

public class StateMessage implements IStateMessage {

    private String message;
    private boolean success;
    private ExceptionState exceptionState;

    @JsonCreator
    public StateMessage(@JsonProperty("message") String message,
                        @JsonProperty("success") boolean success,
                        @JsonProperty(value = "exceptionState") ExceptionState exceptionState) {
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
