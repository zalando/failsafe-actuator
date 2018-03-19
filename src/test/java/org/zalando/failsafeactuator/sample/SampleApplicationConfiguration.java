package org.zalando.failsafeactuator.sample;

import net.jodah.failsafe.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleApplicationConfiguration {

  @Bean(name = "testBreaker")
  public CircuitBreaker createTestBreaker() {
    return new CircuitBreaker();
  }

  @Bean(name = "delayBreaker")
  public CircuitBreaker createDelayBreaker() {
    return new CircuitBreaker();
  }


}
