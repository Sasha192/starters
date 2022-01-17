package org.wpstarters.commontoolsstarter.throttle;

import org.wpstarters.commontoolsstarter.exceptions.ThrottledException;

public interface IThrottleService {

    boolean allow(String fingerPrint) throws ThrottledException;

    void postProcess(String fingerPrint);

}
