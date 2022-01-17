package org.wpstarters.commontoolsstarter.throttle;

import org.wpstarters.commontoolsstarter.exceptions.ThrottledException;

import javax.servlet.http.HttpServletRequest;

public interface IThrottleService {

    boolean allow(HttpServletRequest request) throws ThrottledException;

}
