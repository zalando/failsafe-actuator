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

import net.jodah.failsafe.CircuitBreaker;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;
import org.zalando.failsafeactuator.aspect.Failsafe;
import org.zalando.failsafeactuator.service.FailsafeBreaker;

import java.lang.annotation.Annotation;

/** Configuration class which enables the usage of {@link FailsafeBreaker} annotation. */
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
public class FailsafeInjectionConfiguration {

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public CircuitBreaker circuitBreaker(InjectionPoint ip) {
    Failsafe annotation = null;
    for (Annotation a : ip.getAnnotations()) {
      if (a instanceof Failsafe) {
        annotation = (Failsafe) a;
        break;
      }
    }
    return circuitBreakerRegistry.getOrCreate(annotation.value());
  }
}
