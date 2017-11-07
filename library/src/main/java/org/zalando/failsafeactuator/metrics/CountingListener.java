package org.zalando.failsafeactuator.metrics;

import net.jodah.failsafe.Listeners;
import org.springframework.boot.actuate.metrics.CounterService;

/**
 * Implementation of {@link Listeners} which invokes Springs {@link CounterService} to count the
 * successful and failed calls.
 *
 * @param <R>
 */
public class CountingListener<R> extends Listeners<R> {

  public static final String SUCCESS = ".success";
  public static final String FAILURE = ".failure";

  private final CounterService counterService;
  private final String identifier;

  public CountingListener(final CounterService counterService, final String identifier) {
    this.counterService = counterService;
    this.identifier = identifier;
  }

  @Override
  public void onSuccess(final R result) {
    counterService.increment(identifier + SUCCESS);
  }


  @Override
  public void onFailure(final R result, final Throwable failure) {
    counterService.increment(identifier + FAILURE);
  }
}
