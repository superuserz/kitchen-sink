package com.kitchensink.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kitchensink.user.logging.ControllerLoggingAspect;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ControllerLoggingAspectTest {

	@InjectMocks
	private ControllerLoggingAspect aspect;

	@Mock
	private ProceedingJoinPoint joinPoint;

	@Mock
	private HttpServletRequest request;

	@Mock
	private ServletRequestAttributes attributes;

	@Mock
	private Signature signature;

	@BeforeEach
	void setUp() {
		// Set static RequestContextHolder with mock attributes
		RequestContextHolder.setRequestAttributes(attributes);
		when(attributes.getRequest()).thenReturn(request);
	}

	@Test
	void testLogAround_success() throws Throwable {
		// Arrange
		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn("/api/test");
		when(joinPoint.getSignature()).thenReturn(signature);
		when(signature.toShortString()).thenReturn("TestController.testMethod()");
		when(joinPoint.proceed()).thenReturn("Success");

		// Act
		Object result = aspect.logAround(joinPoint);

		// Assert
		assertEquals("Success", result);
	}

	@Test
	void testLogAround_exception() throws Throwable {
		// Arrange
		when(request.getMethod()).thenReturn("POST");
		when(request.getRequestURI()).thenReturn("/api/fail");
		when(joinPoint.getSignature()).thenReturn(signature);
		when(signature.toShortString()).thenReturn("FailController.failMethod()");
		when(joinPoint.proceed()).thenThrow(new RuntimeException("Something went wrong"));

		// Act & Assert
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> aspect.logAround(joinPoint));
		assertEquals("Something went wrong", thrown.getMessage());
	}
}