/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Zalando SE
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.config;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.util.Duration;
import net.jodah.failsafe.util.Ratio;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;


import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("failsafe-config")
public class FailsafeDefaultConfigurationTest {

  @Autowired
  private CircuitBreakerRegistry circuitBreakerRegistry;

  @Test
  public void defaultDelayIs10Seconds() throws Exception {
    CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate("default");
    assertThat(breaker.getDelay()).isEqualToComparingFieldByField(new Duration(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void defaultFailureThresholdIs1And10() throws Exception {
    CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate("default");
    assertThat(breaker.getFailureThreshold()).isEqualToComparingFieldByField(new Ratio(1, 10));
  }

  @Test
  public void defaultSuccessThresholdIs2And20() throws Exception {
    CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate("default");
    assertThat(breaker.getSuccessThreshold()).isEqualToComparingFieldByField(new Ratio(2, 20));
  }

  @Test
  public void defaultTimeoutIs20Seconds() throws Exception {
    CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate("default");
    assertThat(breaker.getTimeout()).isEqualToComparingFieldByField(new Duration(20000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void customBreakerDelayIs15Seconds() throws Exception {
    CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate("test");
    assertThat(breaker.getDelay()).isEqualToComparingFieldByField(new Duration(15000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void customBreakerFailureThresholdIs2And12() throws Exception {
    CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate("test");
    assertThat(breaker.getFailureThreshold()).isEqualToComparingFieldByField(new Ratio(2, 12));
  }

  @Test
  public void customBreakerSuccessThresholdIs3And22() throws Exception {
    CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate("test");
    assertThat(breaker.getSuccessThreshold()).isEqualToComparingFieldByField(new Ratio(3, 22));
  }

  @Test
  public void customBreakerTimeoutIs25Seconds() throws Exception {
    CircuitBreaker breaker = circuitBreakerRegistry.getOrCreate("test");
    assertThat(breaker.getTimeout()).isEqualToComparingFieldByField(new Duration(25000, TimeUnit.MILLISECONDS));
  }
}
