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
package org.zalando.failsafeactuator.endpoint;

import net.jodah.failsafe.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.stereotype.Component;
import org.zalando.failsafeactuator.endpoint.domain.CircuitBreakerState;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link AbstractEndpoint} for Failsafe purposes.
 *
 * <p>It will return all names of registered {@link CircuitBreaker}'s and their state as JSON.
 *
 * @author mpickhan on 29.06.16.
 */
@Component
public class FailsafeEndpoint extends AbstractEndpoint<List<CircuitBreakerState>> {

  private static final String ENDPOINT_ID = "failsafe";
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  @Autowired
  public FailsafeEndpoint(
      final CircuitBreakerRegistry circuitBreakerRegistry,
      @Value("${endpoints.failsafe.sensitive:true}") final boolean sensitive,
      @Value("${endpoints.failsafe.enabled:endpoints.enabled}")final boolean enabled) {
    super(ENDPOINT_ID, sensitive, enabled);
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @Override
  public List<CircuitBreakerState> invoke() {
    final Map<String, CircuitBreaker> breakerMap = this.circuitBreakerRegistry.getConcurrentBreakerMap();
    final List<CircuitBreakerState> breakerStates = new ArrayList<CircuitBreakerState>();

    final List<String> breakersToRemove = new ArrayList<String>();
    for (final String identifier : breakerMap.keySet()) {
      final CircuitBreaker breaker = breakerMap.get(identifier);
      if (breaker == null) {
        //Memorize unreferenced breakers which need to be removed later on
        breakersToRemove.add(identifier);
      } else {
        final CircuitBreakerState state =
            new CircuitBreakerState(identifier, breaker.isClosed(), breaker.isOpen(), breaker.getState().equals(CircuitBreaker.State.HALF_OPEN));
        breakerStates.add(state);
      }
    }
    removeUnreferencedBreakers(breakersToRemove);
    return breakerStates;
  }

  private void removeUnreferencedBreakers(final List<String> breakersToRemove) {
    for (final String identifier : breakersToRemove) {
      this.circuitBreakerRegistry.getConcurrentBreakerMap().remove(identifier);
    }
  }
}
