package org.wpstarters.jwtauthprovider.props;

public class ThrottlingConfigurationProperties {

    private boolean enabled;
    private boolean considerXForward;
    private int timeWindowInSecs;
    private int maxNumberOfRequests;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTimeWindowInSecs() {
        return timeWindowInSecs;
    }

    public void setTimeWindowInSecs(int timeWindowInSecs) {
        this.timeWindowInSecs = timeWindowInSecs;
    }

    public int getMaxNumberOfRequests() {
        return maxNumberOfRequests;
    }

    public void setMaxNumberOfRequests(int maxNumberOfRequests) {
        this.maxNumberOfRequests = maxNumberOfRequests;
    }

    public boolean isConsiderXForward() {
        return considerXForward;
    }

    public void setConsiderXForward(boolean considerXForward) {
        this.considerXForward = considerXForward;
    }
}
