package org.zalando.failsafeactuator.metrics;

import com.codahale.metrics.Meter;
import net.jodah.failsafe.ExecutionContext;
import net.jodah.failsafe.Listeners;

public class DropwizardListener<R> extends Listeners<R> {

  private final Meter failureMeter;
  private final Meter successMeter;

  public DropwizardListener(final Meter failureMeter, final Meter successMeter) {
    this.failureMeter = failureMeter;
    this.successMeter = successMeter;
  }

  @Override
  public void onSuccess(final R result) {
    successMeter.mark();
  }

  @Override
  public void onSuccess(final R result, final ExecutionContext context) {
    successMeter.mark();
  }

  @Override
  public void onFailure(final R result, final Throwable failure) {
    failureMeter.mark();
  }

  @Override
  public void onFailure(final R result, final Throwable failure, final ExecutionContext context) {
    failureMeter.mark();
  }
}
