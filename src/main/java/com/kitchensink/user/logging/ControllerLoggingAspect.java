package com.kitchensink.user.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class ControllerLoggingAspect {

	private static final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);

	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void controllerMethods() {
	}

	@Around("controllerMethods()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();

		String httpMethod = request.getMethod();
		String uri = request.getRequestURI();
		String methodSignature = joinPoint.getSignature().toShortString();

		logger.info("Entering [{} {}] - {}", httpMethod, uri, methodSignature);

		long startTime = System.currentTimeMillis();

		Object result;
		try {
			result = joinPoint.proceed();
		} catch (Throwable ex) {
			logger.error("Exception in [{} {}] - {}: {}", httpMethod, uri, methodSignature, ex.getMessage(), ex);
			throw ex;
		}

		long elapsedTime = System.currentTimeMillis() - startTime;
		logger.info("Exiting  [{} {}] - {} ({} ms)", httpMethod, uri, methodSignature, elapsedTime);

		return result;
	}
}