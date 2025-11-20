package com.funa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringBootStarterKitApplication {

  private static final Logger logger =
      LoggerFactory.getLogger(SpringBootStarterKitApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(SpringBootStarterKitApplication.class, args);
  }

  @Bean
  public CommandLineRunner logDemo() {
    return args -> {
      logger.trace("TRACE Message");
      logger.debug("DEBUG Message");
      logger.info("INFO  Message");
      logger.warn("WARN  Message");
      logger.error("ERROR Message");

      // Log with exception
      try {
        throw new Exception("Test Exception");
      } catch (Exception e) {
        logger.error("An exception occurred!", e);
      }
    };
  }
}
