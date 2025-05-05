package com.kitchensink.user.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

	private final Map<String, Bucket> buckets = new ConcurrentHashMap<String, Bucket>();

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

	// 5 attempts per minute
	private Bucket newBucket(String key) {
		return Bucket.builder().addLimit(limit -> limit.capacity(5).refillIntervally(5, Duration.ofMinutes(1))).build();
	}

}
