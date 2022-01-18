package org.wpstarters.jwtauthprovider.exceptions;

import org.wpstarters.commontoolsstarter.exceptions.ThrottledExceptionState;

public class ThrottledException extends Exception {

    private final org.wpstarters.commontoolsstarter.exceptions.ThrottledExceptionState throttledExceptionState;
    private final long delayInMs;

    public ThrottledException(String msg, long delayInMs, org.wpstarters.commontoolsstarter.exceptions.ThrottledExceptionState throttledExceptionState) {
        super(msg);
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
