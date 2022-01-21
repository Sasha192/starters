package org.wpstarters.jwtauthprovider.throttle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wpstarters.jwtauthprovider.config.context.RequestFingerprintUtil;
import org.wpstarters.jwtauthprovider.dto.IAuthenticationRequest;
import org.wpstarters.jwtauthprovider.dto.SocialAccountInfo;
import org.wpstarters.jwtauthprovider.exceptions.ExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ExtendedAuthenticationException;
import org.wpstarters.jwtauthprovider.exceptions.ThrottledException;
import org.wpstarters.jwtauthprovider.model.ProviderType;
import org.wpstarters.jwtauthprovider.service.IUserVerificationService;

public class ThrottlingUserVerificationWrapper implements IUserVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(ThrottlingUserVerificationWrapper.class);

    private final IThrottleService throttleService;
    private final IUserVerificationService verificationService;

    public ThrottlingUserVerificationWrapper(IUserVerificationService verificationService, IThrottleService throttleService) {
        this.throttleService = throttleService;
        this.verificationService = verificationService;
    }

    @Override
    public void sendVerificationForRequest(IAuthenticationRequest authencationRequest) throws ExtendedAuthenticationException {

        String usernameFingerprint = authencationRequest.getId();
        String ipAddress = RequestFingerprintUtil.requestIpAddress();

        try {
            if (throttleService.allow(usernameFingerprint) && throttleService.allow(ipAddress)) {

                verificationService.sendVerificationForRequest(authencationRequest);

            }

            throw new ExtendedAuthenticationException("You was throttled", ExceptionState.THROTTLED);

        } catch (ThrottledException e) {

            logger.error(e.getMessage(), e);
            throw new ExtendedAuthenticationException("Internal Server Error", ExceptionState.INTERNAL_SERVER_ERROR);


        }

    }

    @Override
    public boolean verifyCodeForRequest(IAuthenticationRequest authencationRequest) throws ExtendedAuthenticationException {

        String usernameFingerprint = authencationRequest.getId();
        String ipAddress = RequestFingerprintUtil.requestIpAddress();

        try {
            if (throttleService.allow(usernameFingerprint) && throttleService.allow(ipAddress)) {

                throttleService.clean(usernameFingerprint);
                throttleService.clean(ipAddress);
                return verificationService.verifyCodeForRequest(authencationRequest);

            }

            throw new ExtendedAuthenticationException("You was throttled", ExceptionState.THROTTLED);

        } catch (ThrottledException e) {

            logger.error(e.getMessage(), e);
            throw new ExtendedAuthenticationException("Internal Server Error", ExceptionState.INTERNAL_SERVER_ERROR);


        }

    }

    @Override
    public SocialAccountInfo verifySocialAccount(String authorizationCode, ProviderType provider) {

        String ipAddress = RequestFingerprintUtil.requestIpAddress();

        try {
            if (throttleService.allow(ipAddress)) {

                throttleService.clean(ipAddress);
                return verificationService.verifySocialAccount(authorizationCode, provider);

            }

            throw new ExtendedAuthenticationException("You was throttled", ExceptionState.THROTTLED);

        } catch (ThrottledException e) {

            logger.error(e.getMessage(), e);
            throw new ExtendedAuthenticationException("Internal Server Error", ExceptionState.INTERNAL_SERVER_ERROR);


        }

    }
}
