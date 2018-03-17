package org.zalando.failsafeactuator.endpoint.domain;

import net.jodah.failsafe.CircuitBreaker;

public class CircuitBreakerState {

  private String name;
  private CircuitBreaker.State state;

  public CircuitBreakerState() {  }

  public CircuitBreakerState(final String name, final CircuitBreaker.State state) {
    this.name = name;
    this.state = state;
  }

  public String getName() {
    return name;
  }

  public CircuitBreaker.State getState() {
    return state;
  }
}
