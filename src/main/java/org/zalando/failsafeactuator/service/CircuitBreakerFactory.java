/**
 * The MIT License (MIT) Copyright (c) 2016 Zalando SE
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.service;

import net.jodah.failsafe.CircuitBreaker;

import org.zalando.failsafeactuator.endpoint.FailsafeEndpoint;

/** Factory Class to create {@link CircuitBreaker}'s which are registered in SpringContext and can therefore be exposed by {@link FailsafeEndpoint}. */
public class CircuitBreakerFactory {

  private CircuitBreakerRegistry registry;

  public CircuitBreakerFactory(final CircuitBreakerRegistry registry) {
    this.registry = registry;
  }

  /**
   * Creates a new {@link CircuitBreaker} which can be used in regular way and is registered in the SpringContext.
   *
   * @param identifier which will be shown in the output of the {@link FailsafeEndpoint}.
   * @return new Instance of a {@link CircuitBreaker}
   */
  public CircuitBreaker createCircuitBreaker(final String identifier) {
    final CircuitBreaker breaker = new CircuitBreaker();
    registry.registerCircuitBreaker(breaker, identifier);
    return breaker;
  }

  /**
   * Returns the registered {@link CircuitBreaker} for the given identifier or creates a new one.
   *
   * @param identifier which will be shown in the output of the {@link FailsafeEndpoint}.
   * @return an already registered or a new Instance of a {@link CircuitBreaker}
   */
  public CircuitBreaker getOrCreate(final String identifier) {
    if (registry.contains(identifier)) {
      return registry.get(identifier);
    }
    return createCircuitBreaker(identifier);
  }
}
