package org.wpstarters.jwtauthprovider.throttle;

import javax.servlet.http.HttpServletRequest;

public class IpFingerprintOperator implements IToFingerprintOperator {

    private final boolean considerXHeaders;

    public IpFingerprintOperator(boolean considerXHeaders) {
        this.considerXHeaders = considerXHeaders;
    }

    @Override
    public String apply(HttpServletRequest request) {

        String xHeaderValue = retrieveXHeaderValue(request);
        if (considerXHeaders && xHeaderValue != null) {
            return xHeaderValue;
        }
        return request.getRemoteAddr();

    }

    private String retrieveXHeaderValue(HttpServletRequest request) {

        if (request.getHeader("X-Forwarded-For") != null) {
            return request.getHeader("X-Forwarded-For");
        } else if (request.getHeader("x-forwarded-for") != null) {
            return request.getHeader("x-forwarded-for");
        } else {
            return null;
        }

    }


}
