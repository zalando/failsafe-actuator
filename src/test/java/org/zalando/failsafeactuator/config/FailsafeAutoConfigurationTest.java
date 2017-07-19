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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.failsafeactuator.config.FailsafeAutoConfigurationTest.FailsafeAutoConfigraionTestConfiguration.CircuitBreakerConstructorInjection;
import org.zalando.failsafeactuator.config.FailsafeAutoConfigurationTest.FailsafeAutoConfigraionTestConfiguration.CircuitBreakerFieldInjection;
import org.zalando.failsafeactuator.config.FailsafeAutoConfigurationTest.FailsafeAutoConfigraionTestConfiguration.CircuitBreakerSetterInjection;
import org.zalando.failsafeactuator.service.FailsafeBreaker;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
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
  @Import(FailsafeAutoConfiguration.class)
  @ComponentScan
  public static class FailsafeAutoConfigraionTestConfiguration {

    @Component
    public static class CircuitBreakerConstructorInjection {
      final CircuitBreaker breaker;

      public CircuitBreakerConstructorInjection(@FailsafeBreaker("constructor") CircuitBreaker breaker) {
        this.breaker = breaker;
      }
    }

    @Component
    public static class CircuitBreakerFieldInjection {
      @Autowired
      @FailsafeBreaker("field")
      private CircuitBreaker breaker;
    }

    @Component
    public static class CircuitBreakerSetterInjection {
      private CircuitBreaker breaker;

      @Autowired
      public void setBreaker(@FailsafeBreaker("setter") CircuitBreaker breaker) {
        this.breaker = breaker;
      }
    }
  }
}
