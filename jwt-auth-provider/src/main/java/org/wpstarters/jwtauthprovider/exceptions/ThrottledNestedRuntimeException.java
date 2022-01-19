package org.wpstarters.jwtauthprovider.exceptions;

import org.springframework.core.NestedRuntimeException;
import org.wpstarters.commontoolsstarter.exceptions.ThrottledExceptionState;

public class ThrottledNestedRuntimeException extends NestedRuntimeException {

    private final ThrottledExceptionState throttledExceptionState;
    private final long delayInMs;

    public ThrottledNestedRuntimeException(Throwable e, String msg, long delayInMs, ThrottledExceptionState throttledExceptionState) {
        super(msg, e);
        this.throttledExceptionState = throttledExceptionState;
        this.delayInMs = delayInMs;

    }

    public ThrottledExceptionState getExceptionState() {
        return throttledExceptionState;
    }

    public long getDelayInMs() {
        return delayInMs;
    }
}
