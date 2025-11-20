package com.funa.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

  private static final String TRACE_ID = "traceId";
  private static final String USER_ID = "userId";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Generate unique trace ID for each request
      String traceId = UUID.randomUUID().toString();
      MDC.put(TRACE_ID, traceId);

      // Add trace ID to response headers for client debugging
      response.addHeader("X-Trace-Id", traceId);

      // If authenticated, add user ID to MDC
      // This is a placeholder - actual implementation depends on your authentication mechanism
      // SecurityContextHolder.getContext().getAuthentication() might be used in a real
      // implementation

      filterChain.doFilter(request, response);
    } finally {
      // Always clear MDC after the request is processed
      MDC.clear();
    }
  }
}
