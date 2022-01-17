package org.wpstarters.commontoolsstarter.exceptions;

public class ThrottledException extends Exception {

    private final ThrottledExceptionState throttledExceptionState;
    private final long delayInMs;

    public ThrottledException(String msg, long delayInMs, ThrottledExceptionState throttledExceptionState) {
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
