package com.funa.common.exception;

import com.funa.common.logging.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/** Global exception handler for the application. Handles exceptions and logs them consistently. */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles RuntimeException and its subclasses.
   *
   * @param ex The exception to handle
   * @return A ResponseEntity with error details
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
    logger.error("Runtime exception occurred: {}", ex.getMessage(), ex);
    return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles Exception and its subclasses.
   *
   * @param ex The exception to handle
   * @return A ResponseEntity with error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
    logger.error("Exception occurred: {}", ex.getMessage(), ex);
    return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Builds a standardized error response.
   *
   * @param ex The exception
   * @param status The HTTP status
   * @return A ResponseEntity with error details
   */
  private ResponseEntity<Map<String, Object>> buildErrorResponse(Exception ex, HttpStatus status) {
    Map<String, Object> body = new HashMap<>();
    body.put("timestamp", LocalDateTime.now().toString());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", ex.getMessage());
    body.put("traceId", LoggingUtils.getTraceId());

    return new ResponseEntity<>(body, status);
  }
}
