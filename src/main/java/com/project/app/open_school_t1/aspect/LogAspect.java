package com.project.app.open_school_t1.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    private final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(public * com.project.app.open_school_t1.controller.*.*(..))")
    public void logExecution(JoinPoint joinPoint) {
        log.info("Method was executed: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(public * com.project.app.open_school_t1.controller.*.*(..))", returning = "result")
    public void logReturning(JoinPoint joinPoint, Object result) {
        log.info("Method was executed: {}, returning: {}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "execution(public * com.project.app.open_school_t1.service.TaskServiceImplementation.*(..)))", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        log.error("Exception raised: {} in method: {}", exception, joinPoint.getSignature().getName());
    }

    @Around("execution(public * com.project.app.open_school_t1.controller.*.*(..))")
    public Object logTimeExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            log.info("Method execution time: {}", (end - start));
            return result;

        } catch (Throwable throwable) {
            log.error("Exception: {} in method: {}", throwable, joinPoint.getSignature().getName());
            throw throwable;
        }
    }

}
