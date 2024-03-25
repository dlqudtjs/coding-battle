package com.dlqudtjs.codingbattle.common.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LogConfig {

    @Pointcut("execution(* com.dlqudtjs.codingbattle.controller..*.*(..)) && !@annotation(com.dlqudtjs.codingbattle.common.log.NoLogging)")
    public void allController() {
    }

    @Pointcut("execution(* com.dlqudtjs.codingbattle.service..*.*(..)) && !@annotation(com.dlqudtjs.codingbattle.common.log.NoLogging)")
    public void allService() {
    }

    @Pointcut("execution(* com.dlqudtjs.codingbattle.repository..*.*(..)) && !@annotation(com.dlqudtjs.codingbattle.common.log.NoLogging)")
    public void allRepository() {
    }

    @Around("allController() || allService() || allRepository()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.info("Start - {}.{}", className, methodName);
        Object result = joinPoint.proceed();
        log.info("End - {}.{}", className, methodName);
        return result;

//        log.info("==============================execution method==============================");
//        log.info("Target: {}", joinPoint.getTarget());
//        log.info("Signature: {}", joinPoint.getSignature().getName());
//
//        Object result = joinPoint.proceed();
//        return result;
    }
}
