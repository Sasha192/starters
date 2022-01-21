package org.wpstarters.jwtauthprovider.throttle;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wpstarters.jwtauthprovider.config.context.RequestFingerprintUtil;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.exceptions.ThrottledException;
import org.wpstarters.jwtauthprovider.model.CustomUserDetails;
import org.wpstarters.jwtauthprovider.service.ITokenService;

public class ThrottlingTokenServiceWrapper implements ITokenService {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingTokenServiceWrapper.class);

    private final ITokenService tokenService;
    private final IThrottleService throttleService;

    public ThrottlingTokenServiceWrapper(ITokenService tokenService, IThrottleService throttleService) {
        this.tokenService = tokenService;
        this.throttleService = throttleService;
    }

    @Override
    public String generateJwtToken(CustomUserDetails userDetails) throws JsonProcessingException {

        String usernameFingerprint = userDetails.getUsername();
        String fingerPrint = RequestFingerprintUtil.requestIpAddress();

        try {
            if (throttleService.allow(usernameFingerprint) && throttleService.allow(fingerPrint)) {

                throttleService.clean(usernameFingerprint);
                throttleService.clean(fingerPrint);
                return tokenService.generateJwtToken(userDetails);

            }

            throw new ExtendedAuthenticationException("You was throttled", ExceptionState.THROTTLED);

        } catch (ThrottledException e) {

            logger.error(e.getMessage(), e);
            throw new ExtendedAuthenticationException("Internal Server Error", ExceptionState.INTERNAL_SERVER_ERROR);


        }


    }

    @Override
    public String refreshToken(String jwtToken) throws JsonProcessingException {
        String usernameFingerprint = tokenService.getUserNameFromJwtToken(jwtToken);
        String fingerPrint = RequestFingerprintUtil.requestIpAddress();

        try {
            if (throttleService.allow(usernameFingerprint) && throttleService.allow(fingerPrint)) {

                throttleService.clean(usernameFingerprint);
                throttleService.clean(fingerPrint);
                return tokenService.refreshToken(jwtToken);

            }

            throw new ExtendedAuthenticationException("You was throttled", ExceptionState.THROTTLED);

        } catch (ThrottledException e) {

            logger.error(e.getMessage(), e);
            throw new ExtendedAuthenticationException("Internal Server Error", ExceptionState.INTERNAL_SERVER_ERROR);


        }
    }

    @Override
    public String getUserNameFromJwtToken(String token) {
        return tokenService.getUserNameFromJwtToken(token);
    }

    @Override
    public boolean validateJwtToken(String authToken) throws ExtendedAuthenticationException {
        return tokenService.validateJwtToken(authToken);
    }

}
