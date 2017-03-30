package org.zalando.failsafeactuator.config;

import net.jodah.failsafe.CircuitBreaker;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.zalando.failsafeactuator.service.CircuitBreakerFactory;
import org.zalando.failsafeactuator.service.FailsafeBreaker;

import java.lang.annotation.Annotation;

/** Configuration class which enables the usage of {@link FailsafeBreaker} annotation. */
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
public class FailsafeInjectionConfiguration {

  @Autowired
  private CircuitBreakerFactory circuitBreakerFactory;

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public CircuitBreaker circuitBreaker(InjectionPoint ip) {
    FailsafeBreaker annotation = null;
    for (Annotation a : ip.getAnnotations()) {
      if (a instanceof FailsafeBreaker) {
        annotation = (FailsafeBreaker) a;
        break;
      }
    }
    return circuitBreakerFactory.getOrCreate(annotation.value());
  }
}
