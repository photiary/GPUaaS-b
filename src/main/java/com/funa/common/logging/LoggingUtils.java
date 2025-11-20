package com.funa.common.logging;

import org.slf4j.MDC;

/**
 * Utility class for logging operations. Provides methods to add and remove values from the MDC
 * (Mapped Diagnostic Context).
 */
public class LoggingUtils {

  private static final String USER_ID = "userId";

  /**
   * Sets the user ID in the MDC. This should be called when a user is authenticated.
   *
   * @param userId the ID of the authenticated user
   */
  public static void setUserId(String userId) {
    if (userId != null && !userId.isEmpty()) {
      MDC.put(USER_ID, userId);
    }
  }

  /** Removes the user ID from the MDC. This should be called when a user session ends. */
  public static void removeUserId() {
    MDC.remove(USER_ID);
  }

  /**
   * Gets the current trace ID from the MDC.
   *
   * @return the current trace ID, or null if not set
   */
  public static String getTraceId() {
    return MDC.get("traceId");
  }

  /**
   * Gets the current user ID from the MDC.
   *
   * @return the current user ID, or null if not set
   */
  public static String getUserId() {
    return MDC.get(USER_ID);
  }
}
