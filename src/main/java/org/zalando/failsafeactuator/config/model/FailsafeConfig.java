package org.zalando.failsafeactuator.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
public class FailsafeConfig {

  private static final int DEFAULT_DELAY = 1;
  private static final int DEFAULT_SUCCESS_THRESHOLD = 1;
  private static final int DEFAULT_FAILURE_THRESHOLD = 20;

  private final Breaker defaults = new Breaker() {{
    setDelay(Duration.ofMinutes(DEFAULT_DELAY));
    setSuccessThreshold(new Threshold(DEFAULT_SUCCESS_THRESHOLD));
    setFailureThreshold(new Threshold(DEFAULT_FAILURE_THRESHOLD));
  }};

  private final Map<String, Breaker> breaker = new HashMap<>();

  @Data
  public static class Breaker {
    private Duration timeout;
    private Duration delay;
    private Threshold successThreshold;
    private Threshold failureThreshold;
  }

  @Data
  @AllArgsConstructor
  public static class Threshold {
    private Integer executions;
    private Integer threshold;

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
}
