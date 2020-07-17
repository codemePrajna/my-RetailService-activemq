package com.myRetail.config;

import org.aspectj.lang.annotation.Pointcut;

public class PointCutConfig {

    @Pointcut("execution(* com.construct..*(..)) || execution(* com.services..*(..))")
    public void genericLayerExecution(){}

    @Pointcut("execution(* com.construct.*.*(..))")
    public void constructLayerExecution(){}
}
