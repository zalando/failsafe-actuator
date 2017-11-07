package org.zalando.failsafeactuator.config;

import static org.junit.Assert.assertNotNull;

import net.jodah.failsafe.CircuitBreaker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.failsafeactuator.aspect.Failsafe;
import org.zalando.failsafeactuator.config.FailsafeAutoConfigurationTest.FailsafeAutoConfigurationTestConfiguration;
import org.zalando.failsafeactuator.config.FailsafeAutoConfigurationTest.FailsafeAutoConfigurationTestConfiguration.CircuitBreakerConstructorInjection;
import org.zalando.failsafeactuator.config.FailsafeAutoConfigurationTest.FailsafeAutoConfigurationTestConfiguration.CircuitBreakerFieldInjection;
import org.zalando.failsafeactuator.config.FailsafeAutoConfigurationTest.FailsafeAutoConfigurationTestConfiguration.CircuitBreakerSetterInjection;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        FailsafeAutoConfigurationTestConfiguration.class,
        FailsafeAutoConfiguration.class,
        FailsafeAspectAutoConfiguration.class,
        FailsafeInjectionConfiguration.class,
        MetricRepositoryAutoConfiguration.class})
public class FailsafeAutoConfigurationTest {

  @Autowired
  private CircuitBreakerConstructorInjection constructorInjection;

  @Autowired
  private CircuitBreakerFieldInjection fieldInjection;

  @Autowired
  private CircuitBreakerSetterInjection setterInjection;

  @Test
  public void circuitBreakerConstructorInjection() throws Exception {
    assertNotNull(constructorInjection);
    assertNotNull(constructorInjection.breaker);
  }

  @Test
  public void circuitBreakerFieldInjection() throws Exception {
    assertNotNull(fieldInjection);
    assertNotNull(fieldInjection.breaker);
  }

  @Test
  public void circuitBreakerSetterInjection() throws Exception {
    assertNotNull(setterInjection);
    assertNotNull(setterInjection.breaker);
  }

  @Configuration
  public static class FailsafeAutoConfigurationTestConfiguration {

    @Component
    public static class CircuitBreakerConstructorInjection {
      private final CircuitBreaker breaker;

      public CircuitBreakerConstructorInjection(
          @Failsafe("constructor") final CircuitBreaker breaker) {
        this.breaker = breaker;
      }
    }

    @Component
    public static class CircuitBreakerFieldInjection {
      @Autowired
      @Failsafe("field")
      private CircuitBreaker breaker;
    }

    @Component
    public static class CircuitBreakerSetterInjection {
      private CircuitBreaker breaker;

      @Autowired
      public void setBreaker(@Failsafe("setter") final CircuitBreaker breaker) {
        this.breaker = breaker;
      }
    }
  }
}
