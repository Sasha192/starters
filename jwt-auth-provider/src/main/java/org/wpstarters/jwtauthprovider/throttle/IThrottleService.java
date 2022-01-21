package org.wpstarters.jwtauthprovider.throttle;

import org.wpstarters.jwtauthprovider.exceptions.ThrottledException;

public interface IThrottleService {

    boolean allow(String fingerPrint) throws ThrottledException;

    void postProcess(String fingerPrint);

    void clean(String fingerPrint);

}
