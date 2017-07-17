package org.zalando.failsafeactuator.domain;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import net.jodah.failsafe.CircuitBreaker;

/**
 * Implementation of {@link CircuitBreaker} which gathers statistics.
 */
public class SpringBreaker extends CircuitBreaker {

  private static final String FAILED = ".failed";

  private static final String SUCCESS = ".success";

  private final Meter failedMeter;
  private final Meter successMeter;

  /**
   * Constructor for Spring specific Circuit Breaker.
   * @param id which is used to give {@link Meter} a unique name.
   * @param metricRegistry to get a new instance of {@link Meter}.
   */
  public SpringBreaker(final String id, final MetricRegistry metricRegistry) {
    super();
    failedMeter = metricRegistry.meter(id+FAILED);
    successMeter = metricRegistry.meter(id + SUCCESS);
  }

  @Override
  public void recordFailure(Throwable failure) {
    failedMeter.mark();
    super.recordFailure(failure);
  }

  @Override
  public  void recordSuccess() {
    successMeter.mark();
    super.recordSuccess();
  }

}
