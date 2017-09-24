package org.zalando.failsafeactuator.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
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
    private Class<? extends Throwable>[] failOn;
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
}
