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
package org.zalando.failsafeactuator.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class FailsafeConfig {

  private static final int DEFAULT_DELAY_IN_MINUTES = 1;
  private static final int DEFAULT_SUCCESS_THRESHOLD_VALUE = 1;
  private static final int DEFAULT_FAILURE_THRESHOLD_VALUE = 20;

  private static final Threshold DEFAULT_SUCCESS_THRESHOLD = new Threshold(DEFAULT_SUCCESS_THRESHOLD_VALUE);
  private static final Threshold DEFAULT_FAILURE_THRESHOLD = new Threshold(DEFAULT_FAILURE_THRESHOLD_VALUE);
  private static final Duration DEFAULT_DELAY = Duration.ofMinutes(DEFAULT_DELAY_IN_MINUTES);

  private final Breaker defaults = new Breaker(DEFAULT_DELAY, DEFAULT_SUCCESS_THRESHOLD, DEFAULT_FAILURE_THRESHOLD);

  private final Map<String, Breaker> breaker = new HashMap<>();

  @Data
  @NoArgsConstructor
  public static class Breaker {
    private Duration timeout;
    private Duration delay;
    private Threshold successThreshold;
    private Threshold failureThreshold;

    Breaker(Duration delay, Threshold successThreshold, Threshold failureThreshold) {
      this.delay = delay;
      this.successThreshold = successThreshold;
      this.failureThreshold = failureThreshold;
    }
  }

  @Data
  @AllArgsConstructor
  public static class Threshold {
    private Integer threshold;
    private Integer executions;

    public Threshold(Integer threshold) {
      this(threshold, threshold);
    }
  }

  public void populateBreakersWithDefaultsIfNecessary() {
    Collection<Breaker> breakers = getBreaker().values();
    for (FailsafeConfig.Breaker breaker : breakers) {
      timeoutDefault(breaker);
      delayDefault(breaker);
      successThresholdDefault(breaker);
      failureThresholdDefault(breaker);
    }
  }

  private void timeoutDefault(Breaker breaker) {
    if (breaker.getTimeout() == null) {
      breaker.setTimeout(defaults.timeout);
    }
  }

  private void delayDefault(Breaker breaker) {
    if(breaker.getDelay() == null) {
      breaker.setDelay(defaults.delay);
    }
  }

  private void successThresholdDefault(Breaker breaker) {
    if (breaker.getSuccessThreshold() == null) {
      breaker.setSuccessThreshold(defaults.successThreshold);
    }
  }

  private void failureThresholdDefault(Breaker breaker) {
    if (breaker.getFailureThreshold() == null) {
      breaker.setFailureThreshold(defaults.failureThreshold);
    }
  }

  public Breaker getConfigForBreaker(String breakerName) {
    return breaker.getOrDefault(breakerName, defaults);
  }
}
