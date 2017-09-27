package org.zalando.failsafeactuator.aspect;

public class FailsafeBreakerWrappedException extends RuntimeException {
  public FailsafeBreakerWrappedException(Throwable throwable) {
    super(throwable);
  }
}
