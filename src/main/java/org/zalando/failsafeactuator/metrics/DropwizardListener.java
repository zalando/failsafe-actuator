package org.zalando.failsafeactuator.metrics;

import net.jodah.failsafe.ExecutionContext;
import net.jodah.failsafe.Listeners;
import org.springframework.boot.actuate.metrics.CounterService;

public class DropwizardListener<R> extends Listeners<R> {

  public static final String SUCCESS = ".success";
  public static final String FAILURE = ".failure";

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
