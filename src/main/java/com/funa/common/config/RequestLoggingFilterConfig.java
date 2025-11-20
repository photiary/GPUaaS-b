package com.funa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/** Configuration for request logging. Sets up a filter to log request details. */
@Configuration
public class RequestLoggingFilterConfig {

  /**
   * Creates a CommonsRequestLoggingFilter bean to log request details.
   *
   * @return the configured CommonsRequestLoggingFilter
   */
  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setMaxPayloadLength(10000);
    loggingFilter.setIncludeHeaders(true);
    loggingFilter.setBeforeMessagePrefix("REQUEST DATA : ");
    loggingFilter.setAfterMessagePrefix("RESPONSE DATA : ");
    return loggingFilter;
  }
}
