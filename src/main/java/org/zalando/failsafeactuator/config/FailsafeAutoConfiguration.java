package org.zalando.failsafeactuator.config;

import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.zalando.failsafeactuator.endpoint.FailsafeEndpoint;
import org.zalando.failsafeactuator.service.CircuitBreakerFactory;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

/** Autoconfiguration for the FailsafeEndpoint. */
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
public class FailsafeAutoConfiguration {

  private CircuitBreakerRegistry circuitBreakerRegistry;

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    circuitBreakerRegistry = new CircuitBreakerRegistry();
    return circuitBreakerRegistry;
  }

  @Bean
  @DependsOn(value = "circuitBreakerRegistry")
  public CircuitBreakerFactory circuitBreakerFactory() {
    return new CircuitBreakerFactory(circuitBreakerRegistry);
  }

  @Bean
  public FailsafeEndpoint createEndpoint() {
    return new FailsafeEndpoint(circuitBreakerRegistry);
  }

  /** Condition to check that the Failsafe endpoint is enabled */
  static class FailsafeCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
      boolean endpointsEnabled = isEnabled(context, "endpoints.", true);
      ConditionMessage.Builder message = ConditionMessage.forCondition("Failsafe");
      if (isEnabled(context, "endpoints.failsafe.", endpointsEnabled)) {
        return ConditionOutcome.match(message.because("enabled"));
      }
      return ConditionOutcome.noMatch(message.because("not enabled"));
    }

    private boolean isEnabled(ConditionContext context, String prefix, boolean defaultValue) {
      RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(context.getEnvironment(), prefix);
      return resolver.getProperty("enabled", Boolean.class, defaultValue);
    }
  }
}
