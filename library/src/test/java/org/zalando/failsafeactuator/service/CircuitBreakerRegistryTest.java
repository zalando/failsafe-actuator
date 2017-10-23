package org.zalando.failsafeactuator.service;

import net.jodah.failsafe.CircuitBreaker;
import org.junit.Before;
import org.junit.Test;

public class CircuitBreakerRegistryTest {

  private static final String ID = "id";

  private CircuitBreakerRegistry registry;

  @Before
  public void init() {
    registry = new CircuitBreakerRegistry();
  }

  @Test(expected = IllegalArgumentException.class)
  public void putNullBreakerTest() {
    registry.registerCircuitBreaker(null, ID);
  }

  @Test(expected = IllegalArgumentException.class)
  public void putNullStringTest() {
    registry.registerCircuitBreaker(new CircuitBreaker(), null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void putEmptyStringTest() {
    registry.registerCircuitBreaker(new CircuitBreaker(), "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void avoidReplacingRegisteredCircuitBreakers() {
    registry.registerCircuitBreaker(new CircuitBreaker(), "ONE");
    registry.registerCircuitBreaker(new CircuitBreaker(), "TWO");
    registry.registerCircuitBreaker(new CircuitBreaker(), "ONE");
  }
}
