/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Zalando SE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.config;

import com.codahale.metrics.MetricRegistry;
import net.jodah.failsafe.CircuitBreaker;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.zalando.failsafeactuator.endpoint.FailsafeEndpoint;
import org.zalando.failsafeactuator.service.CircuitBreakerFactory;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;
import org.zalando.failsafeactuator.service.FailsafeBreaker;

import java.lang.annotation.Annotation;

/** Autoconfiguration for the FailsafeEndpoint. */
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
public class FailsafeAutoConfiguration {

  private CircuitBreakerRegistry circuitBreakerRegistry;
  private MetricRegistry metricRegistry;

  @Bean
  public CircuitBreakerRegistry circuitBreakerRegistry() {
    circuitBreakerRegistry = new CircuitBreakerRegistry();
    return circuitBreakerRegistry;
  }

  @Bean
  public MetricRegistry metricRegistry() {
    this.metricRegistry = new MetricRegistry();
    return metricRegistry;
  }

  @Bean
  @DependsOn(value = "circuitBreakerRegistry")
  public CircuitBreakerFactory circuitBreakerFactory() {
    return new CircuitBreakerFactory(circuitBreakerRegistry, metricRegistry);
  }

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
    return circuitBreakerFactory().getOrCreate(annotation.value());
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
