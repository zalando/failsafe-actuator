/**
 * The MIT License (MIT) Copyright (c) 2016 Zalando SE
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.failsafeactuator.aspect.Failsafe;
import org.zalando.failsafeactuator.config.FailsafeAspectConfigurationTest.FailsafeAutoConfigurationTestConfiguration.FailsafeBreakerMethodProtection;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FailsafeAspectConfigurationTest {

  @Autowired private FailsafeBreakerMethodProtection methodProtection;

  @Test
  public void circuitBreakerWithoutFallback() throws Exception {
    assertThatThrownBy(() -> methodProtection.withoutFallback("withoutFallback"))
        .isInstanceOf(NumberFormatException.class)
        .hasMessageContaining("withoutFallback");
  }

  @Test
  public void circuitBreakerWithFallback() throws Exception {
    assertThat(methodProtection.withFallback("argument")).isEqualTo("withFallback");
  }

  @Test
  public void circuitBreakerWithFailingFallback() throws Exception {
    assertThatThrownBy(() -> methodProtection.withFailingFallback("withFailingFallback"))
        .isInstanceOf(NumberFormatException.class)
        .hasMessageContaining("fallbackCalled");
  }

  @Configuration
  @Import(FailsafeAutoConfiguration.class)
  @ComponentScan
  public static class FailsafeAutoConfigurationTestConfiguration {

    @Component
    public static class FailsafeBreakerMethodProtection {

      @Failsafe("withoutFallBack")
      public String withoutFallback(final String callArg) {
        throw new NumberFormatException(callArg);
      }

      @Failsafe(value = "withFallBack", fallbackMethod = "fallback")
      public String withFallback(final String callArg) {
        throw new NumberFormatException(callArg);
      }

      public String fallback(final String fallbackArg) {
        return "withFallback";
      }

      @Failsafe(value = "withFallBack", fallbackMethod = "failingFallback")
      public String withFailingFallback(final String callArg) {
        throw new NumberFormatException(callArg);
      }

      public String failingFallback(final String fallbackArg) {
        throw new NumberFormatException("fallbackCalled");
      }
    }
  }
}
