package com.funa.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  public void testDemoEndpointAccessWithoutAuth() throws Exception {
    // Test that /api/demo/hello can be accessed without authentication
    mockMvc.perform(get("/api/demo/hello")).andExpect(status().isOk());

    // Test that /api/demo/error can be accessed without authentication
    // Note: This will return 500 because it throws an exception, but it should not be a 401/403
    mockMvc.perform(get("/api/demo/error")).andExpect(status().isInternalServerError());
  }

  @Test
  public void testOtherEndpointsRequireAuth() throws Exception {
    // Test that a non-existent endpoint outside /api/demo requires authentication
    // Spring Security returns 403 Forbidden for unauthenticated requests by default
    mockMvc.perform(get("/api/other")).andExpect(status().isForbidden());
  }
}
