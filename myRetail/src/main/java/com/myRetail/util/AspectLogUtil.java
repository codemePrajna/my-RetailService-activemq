package com.myRetail.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AspectLogUtil {
    @Around("@annotation(com.common.util.TrackTimeUtil)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        log.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");
        return proceed;
    }

    @Before("com.myRetail.config.PointCutConfig.genericLayerExecution()")
    public void before(JoinPoint joinPoint) {
        log.info("Before execution of {}", joinPoint);
    }

    @After("com.myRetail.config.PointCutConfig.genericLayerExecution()")
    public void after(JoinPoint joinPoint) {
        log.info("After execution of {}", joinPoint);
    }

    @AfterThrowing(value = "com.myRetail.config.PointCutConfig.genericLayerExecution()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        log.error("LoggingAspect.afterThrowingAdviceForAllMethods() " + joinPoint.getSignature().getName() + " Exception: " + exception);
    }

}
