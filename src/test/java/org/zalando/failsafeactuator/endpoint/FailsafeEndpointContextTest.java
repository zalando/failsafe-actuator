package org.zalando.failsafeactuator.endpoint;

import net.jodah.failsafe.CircuitBreaker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.failsafeactuator.config.FailsafeAutoConfiguration;
import org.zalando.failsafeactuator.endpoint.domain.CircuitBreakerState;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {FailsafeAutoConfiguration.class, FailsafeEndpointContextTest.CircuitBreakerConfig.class})
public class FailsafeEndpointContextTest {

  private static final String CB_ONE = "one";

  @Autowired
  private FailsafeEndpoint endpoint;

  @Configuration
  public static class CircuitBreakerConfig {
    @Bean(name = CB_ONE)
    public CircuitBreaker createBreaker() {
      return new CircuitBreaker();
    }
  }

  @Test
  public void suchesfullyRegisteredTest() {
    Map<String, CircuitBreakerState> invoke = endpoint.invoke();

    assertNotNull(invoke.get(CB_ONE));
    assertEquals(CB_ONE, invoke.get(CB_ONE).getName());
  }
}
