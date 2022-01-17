package org.wpstarters.commontoolsstarter.throttle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.wpstarters.commontoolsstarter.context.HttpContextHolder;
import org.wpstarters.commontoolsstarter.exceptions.ThrottledException;
import org.wpstarters.commontoolsstarter.exceptions.ThrottledExceptionState;
import org.wpstarters.commontoolsstarter.exceptions.ThrottledNestedRuntimeException;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ThrottleFilterAspect {

    private static final Logger logger = LoggerFactory.getLogger(ThrottleFilterAspect.class);

    private IThrottleService throttleService;

    public ThrottleFilterAspect(IThrottleService throttleService) {
        this.throttleService = throttleService;
    }

    @Around(value = "@annotation(throttleAnnotation)")
    public Object getSessionAfter(ProceedingJoinPoint joinPoint, Throttle throttleAnnotation) {

        String logMessage = "";

        try {
            if (HttpContextHolder.request.get() != null) {

                HttpServletRequest request = HttpContextHolder.request.get();
                if (throttleService.allow(request)) {

                    return joinPoint.proceed();

                } else {

                    throw new ThrottledException("You was throttled", throttleAnnotation.delayInMs(), ThrottledExceptionState.THROTTLED);

                }


            } else {
                logMessage = "Can not get request from HttpContextHolder";
            }

            throw new ThrottledNestedRuntimeException(null, logMessage, 0, ThrottledExceptionState.INTERNAL_SERVER_ERROR);

        } catch (Throwable throwable) {

            logger.error("Logger exception: ", throwable);
            logMessage = "Internal server error";
            throw new ThrottledNestedRuntimeException(throwable, logMessage, 0, ThrottledExceptionState.INTERNAL_SERVER_ERROR);

        }


    }

}
