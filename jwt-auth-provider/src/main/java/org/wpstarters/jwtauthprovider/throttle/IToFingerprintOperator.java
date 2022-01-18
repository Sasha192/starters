package org.wpstarters.jwtauthprovider.throttle;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

public interface IToFingerprintOperator extends Function<HttpServletRequest, String> {

    @Override
    String apply(HttpServletRequest request);
}
