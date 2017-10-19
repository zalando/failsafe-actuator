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

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.zalando.failsafeactuator.service.CircuitBreakerFactory;
import org.zalando.failsafeactuator.service.FailsafeBreaker;

import net.jodah.failsafe.CircuitBreaker;

/** Configuration class which enables the usage of {@link FailsafeBreaker} annotation. */
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
public class FailsafeInjectionConfiguration {

	@Autowired
	private ApplicationContext applicationContext;
	
  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public CircuitBreaker circuitBreaker(InjectionPoint ip) throws Exception {
    FailsafeBreaker annotation = null;
    for (Annotation a : ip.getAnnotations()) {
      if (a instanceof FailsafeBreaker) {
        annotation = (FailsafeBreaker) a;
        break;
      }
    }
    
    CircuitBreakerFactory circuitBreakerFactory = new CircuitBreakerFactory();
    AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
    return (CircuitBreaker) factory.initializeBean(circuitBreakerFactory.getObject(), annotation.value());
  }
}
