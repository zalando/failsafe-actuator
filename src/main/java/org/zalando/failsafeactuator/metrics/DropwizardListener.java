package org.zalando.failsafeactuator.metrics;

import static org.zalando.failsafeactuator.metrics.DropwizardMetric.FAILURE;
import static org.zalando.failsafeactuator.metrics.DropwizardMetric.SUCCESS;

import net.jodah.failsafe.ExecutionContext;
import net.jodah.failsafe.Listeners;
import org.springframework.boot.actuate.metrics.CounterService;

public class DropwizardListener<R> extends Listeners<R> {

  private final CounterService counterService;
  private final String identifier;

  public DropwizardListener(final CounterService counterService, final String identifier) {
    this.counterService = counterService;
    this.identifier = identifier;
  }

  @Override
  public void onSuccess(final R result) {
    counterService.increment(identifier + SUCCESS);
  }

  @Override
  public void onSuccess(final R result, final ExecutionContext context) {
    counterService.increment(identifier + SUCCESS);
  }

  @Override
  public void onFailure(final R result, final Throwable failure) {
    counterService.increment(identifier + FAILURE);
  }

  @Override
  public void onFailure(final R result, final Throwable failure, final ExecutionContext context) {
    counterService.increment(identifier + FAILURE);
  }
}
