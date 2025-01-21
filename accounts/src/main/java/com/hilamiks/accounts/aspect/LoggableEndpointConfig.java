package com.hilamiks.accounts.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggableEndpointConfig {

    @Around("@annotation(LoggableEndpoint)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getThis().getClass());
        log.debug(joinPoint.getSignature().getName() + " execution started");
        Object proceed = joinPoint.proceed();
        log.debug(joinPoint.getSignature().getName() + " execution finished");
        return proceed;
    }

}
