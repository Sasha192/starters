package org.wpstarters.jwtauthprovider.throttle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wpstarters.jwtauthprovider.config.context.RequestFingerPrintHolder;
import org.wpstarters.jwtauthprovider.exceptions.ThrottledException;
import org.wpstarters.jwtauthprovider.exceptions.ThrottledExceptionState;
import org.wpstarters.jwtauthprovider.exceptions.ThrottledNestedRuntimeException;

@Aspect
public class ThrottleFilterAspect {

    private static final Logger logger = LoggerFactory.getLogger(ThrottleFilterAspect.class);

    private final IThrottleService throttleService;

    public ThrottleFilterAspect(IThrottleService throttleService) {
        this.throttleService = throttleService;
    }

    @Around(value = "@annotation(throttlingPerIPAnnotation)")
    public Object getSessionAfter(ProceedingJoinPoint joinPoint, ThrottlingPerIP throttlingPerIPAnnotation) {
        try {

            String fingerPrint = RequestFingerPrintHolder.fingerPrint.get();

            if (throttleService.allow(fingerPrint)) {

                Object returnValue = joinPoint.proceed();
                throttleService.postProcess(fingerPrint);
                return returnValue;

            } else {

                throw new ThrottledException("You was throttled", throttlingPerIPAnnotation.delayInMs(), ThrottledExceptionState.THROTTLED);

            }


        } catch (Throwable throwable) {

            logger.error("Logger exception: ", throwable);
            throw new ThrottledNestedRuntimeException(throwable, "Internal server error", 0, ThrottledExceptionState.INTERNAL_SERVER_ERROR);

        }


    }

}
