package org.wpstarters.jwtauthprovider.throttle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapThrottling implements IThrottleService {

    private static final Map<String, ThrottleTokenPerFingerprint> throttlingMap = new ConcurrentHashMap<String, ThrottleTokenPerFingerprint>();
    private final int timeWindow;
    private final byte maxNumberOfRequestsWithinTimeWindow;

    public ConcurrentHashMapThrottling(int timeWindow, int maxNumberOfRequestsWithinTimeWindow) {
        this.timeWindow = timeWindow;
        this.maxNumberOfRequestsWithinTimeWindow = (byte) (Math.abs(maxNumberOfRequestsWithinTimeWindow) & Byte.MAX_VALUE);
    }

    @Override
    public boolean allow(String fingerPrint) {

        if (throttlingMap.containsKey(fingerPrint)) {
            ThrottleTokenPerFingerprint tokenPerFingerprint = throttlingMap.get(fingerPrint);

            if (tokenPerFingerprint.getNumberOfRequests() < maxNumberOfRequestsWithinTimeWindow) {

                // user did not make more than 10 requests in a minute
                tokenPerFingerprint.increaseNumberOfRequest();
                return true;

            } else {

                if ((System.currentTimeMillis() - tokenPerFingerprint.getTimeWindowStart()) < timeWindow) {

                    // user did make more than {maxNumberOfRequestsWithinTimeWindow} requests within timeWindow
                    return false;

                } else {

                    // user did not make more than {maxNumberOfRequestsWithinTimeWindow} requests within timeWindow
                    throttlingMap.remove(fingerPrint);
                    return true;

                }
            }
        } else {

            // user made request the first time within timeWindow
            ThrottleTokenPerFingerprint tokenPerFingerprint = new ThrottleTokenPerFingerprint(System.currentTimeMillis());
            tokenPerFingerprint.increaseNumberOfRequest();
            throttlingMap.put(fingerPrint, tokenPerFingerprint);
            return true;

        }

    }

    @Override
    public void postProcess(String fingerPrint) {

        if (throttlingMap.containsKey(fingerPrint)) {

            if ((System.currentTimeMillis() - throttlingMap.get(fingerPrint).getTimeWindowStart()) > timeWindow) {

                throttlingMap.remove(fingerPrint);

            }

        }

    }

    private static final class ThrottleTokenPerFingerprint {

        private byte numberOfRequests;
        private final long timeWindowStart;

        public ThrottleTokenPerFingerprint(long timeWindowStart) {
            this.numberOfRequests = 0;
            this.timeWindowStart = timeWindowStart;
        }

        public byte getNumberOfRequests() {
            return numberOfRequests;
        }

        public long getTimeWindowStart() {
            return timeWindowStart;
        }

        void increaseNumberOfRequest() {
            numberOfRequests++;
        }
    }
}
