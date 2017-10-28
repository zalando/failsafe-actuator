package org.zalando.failsafeactuator.config;

import java.lang.annotation.Annotation;
import net.jodah.failsafe.CircuitBreaker;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.zalando.failsafeactuator.aspect.Failsafe;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

/** Configuration class which enables the usage of {@link Failsafe} annotation. */
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
public class FailsafeInjectionConfiguration {

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public CircuitBreaker circuitBreaker(final InjectionPoint ip) {
    Failsafe annotation = null;
    for (final Annotation a : ip.getAnnotations()) {
      if (a instanceof Failsafe) {
        annotation = (Failsafe) a;
        break;
      }
    }
    return circuitBreakerRegistry.getOrCreate(annotation.value());
  }
}
