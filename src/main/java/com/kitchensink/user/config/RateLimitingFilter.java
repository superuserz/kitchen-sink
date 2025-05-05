package com.kitchensink.user.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author manmeetdevgun
 * 
 *         The Class RateLimitingFilter.
 * 
 *         This class is intended to limit the incoming requests to the login
 *         service. to prevent DDOs attacks.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	/** The capacity. */
	@Value("${rate.limit.capacity}")
	private long capacity;

	/** The refill limit. */
	@Value("${rate.limit.refill}")
	private long refillLimit;

	/** The refill duration. */
	@Value("${rate.limit.duration}")
	private long refillDuration;

	/** The buckets. */
	private final Map<String, Bucket> buckets = new ConcurrentHashMap<String, Bucket>();

	/**
	 * Do filter internal.
	 *
	 * @param request     the request
	 * @param response    the response
	 * @param filterChain the filter chain
	 * @throws ServletException the servlet exception
	 * @throws IOException      Signals that an I/O exception has occurred.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if ("/api/login".equals(request.getRequestURI())) {
			String ip = request.getRemoteAddr();
			Bucket bucket = buckets.computeIfAbsent(ip, this::newBucket);

			if (bucket.tryConsume(1)) {
				filterChain.doFilter(request, response);
			} else {
				response.setStatus(429);
				response.getWriter().write("Too many login attempts. Please try again later.");
			}
		} else {
			filterChain.doFilter(request, response);
		}
	}

	/**
	 * New bucket.
	 *
	 * @param key the key
	 * @return the bucket
	 */
	private Bucket newBucket(String key) {
		return Bucket.builder().addLimit(
				limit -> limit.capacity(capacity).refillIntervally(refillLimit, Duration.ofMinutes(refillDuration)))
				.build();
	}

}
