package org.wpstarters.jwtauthprovider.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;

@JsonSerialize(as=IStateMessage.class)
public interface IStateMessage {

    String getMessage();

    boolean isSuccess();

    ExceptionState getRequestState();

}
