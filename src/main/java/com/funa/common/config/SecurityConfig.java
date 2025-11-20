package com.funa.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application. Configures which endpoints require authentication and
 * which don't.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Configures the security filter chain. Allows access to /api/demo/** without authentication.
   * Requires authentication for all other endpoints.
   *
   * @param http the HttpSecurity to configure
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs during configuration
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/api/demo/**")
                    .permitAll() // Allow access to /api/demo/** without authentication
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll() // Allow access to Swagger UI and OpenAPI endpoints
                    .anyRequest()
                    .authenticated() // Require authentication for all other endpoints
            )
        .csrf(AbstractHttpConfigurer::disable); // Disable CSRF for simplicity in this demo

    return http.build();
  }
}
