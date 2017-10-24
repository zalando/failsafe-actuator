/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Zalando SE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.service;

import net.jodah.failsafe.CircuitBreaker;

import org.springframework.util.Assert;
import org.zalando.failsafeactuator.endpoint.FailsafeEndpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;

/**
 * Registry which holds a reference to registered circuit breakers.
 *
 * @author mpickhan on 29.06.16.
 */
public class CircuitBreakerRegistry {

  private static final String ALREADY_REGISTERED_ERROR = "There was a Circuit-Breaker registered already with name : %s ";

  private final Map<String, CircuitBreaker> concurrentBreakerMap = new ConcurrentHashMap<String, CircuitBreaker>();

  /**
   * Will put the {@link CircuitBreaker} into the registry. There is no check which avoids overwriting of identifiers. Therefore be sure that your identifiers
   * are unique, or you want to overwrite the current {@link CircuitBreaker} which is registered with this identifier.
   *
   * @param breaker Which should be added
   * @param name Which is used to identify the CircuitBreaker
   */
  void registerCircuitBreaker(final CircuitBreaker breaker, final String name) {
    Assert.hasText(name, "Name for circuit breaker needs to be set");
    Assert.notNull(breaker, "Circuit breaker to add, can't be null");

    final CircuitBreaker replaced = concurrentBreakerMap.put(name, breaker);
    Assert.isNull(replaced, String.format(ALREADY_REGISTERED_ERROR, name));
  }

  /**
   * Returns the {@link Map} with registered circuit breakers.
   *
   * @return returns the referenced {@link Map}
   */
  public Map<String, CircuitBreaker> getConcurrentBreakerMap() {
    return this.concurrentBreakerMap;
  }

  /**
   * Creates a new {@link CircuitBreaker} which can be used in regular way and is registered in the SpringContext.
   *
   * @param identifier which will be shown in the output of the {@link FailsafeEndpoint}.
   * @return new Instance of a {@link CircuitBreaker}
   */
  private CircuitBreaker createCircuitBreaker(final String identifier) {
    final CircuitBreaker breaker = new CircuitBreaker();
    registerCircuitBreaker(breaker, identifier);
    return breaker;
  }

  /**
   * Returns the registered {@link CircuitBreaker} for the given identifier or creates a new one.
   *
   * @param identifier which will be shown in the output of the {@link FailsafeEndpoint}.
   * @return an already registered or a new Instance of a {@link CircuitBreaker}
   */
  public CircuitBreaker getOrCreate(final String identifier) {
    Assert.hasText(identifier, "Identifier for circuit breaker needs to be set");
    final CircuitBreaker circuitBreaker = concurrentBreakerMap.get(identifier);
    if (circuitBreaker != null) {
      return circuitBreaker;
    }
    return createCircuitBreaker(identifier);
  }

  @PreDestroy
  public void destroy() throws Exception {
    concurrentBreakerMap.clear();
  }
}
