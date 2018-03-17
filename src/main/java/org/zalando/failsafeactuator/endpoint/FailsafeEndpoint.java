package org.zalando.failsafeactuator.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jodah.failsafe.CircuitBreaker;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.zalando.failsafeactuator.endpoint.domain.CircuitBreakerState;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

/**
 * Implementation of {@link Endpoint} for Failsafe purposes.
 *
 * <p>It will return all names of registered {@link CircuitBreaker}'s and their state as JSON.
 *
 * @author mpickhan on 29.06.16.
 */
@Endpoint(id = "failsafe")
public class FailsafeEndpoint {

  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final ApplicationContext context;

  public FailsafeEndpoint(final CircuitBreakerRegistry circuitBreakerRegistry, final ApplicationContext context) {
    this.circuitBreakerRegistry = circuitBreakerRegistry;
    this.context = context;
  }

  @ReadOperation
  public Map<String, CircuitBreakerState> listCircuitBreakerStates() {
    final Map<String, CircuitBreaker> breakerMap = circuitBreakerRegistry.getConcurrentBreakerMap();

    if(!breakerMap.isEmpty()) {
      return handleWithMap(breakerMap);
    } else {
      return handleWithApplicationContext();
    }

  }

  private Map<String, CircuitBreakerState> handleWithApplicationContext() {
    final Map<String, CircuitBreakerState> breakerStates = new HashMap<>();

    final Map<String, CircuitBreaker> beanNamesForType = context.getBeansOfType(CircuitBreaker.class, false, false);

    if(beanNamesForType == null) {
      return breakerStates;
    }

    for(String key : beanNamesForType.keySet()) {
      CircuitBreaker breaker = beanNamesForType.get(key);

      final CircuitBreakerState state =
              new CircuitBreakerState(key, breaker.getState());
      breakerStates.put(key, state);
    }

    return breakerStates;
  }

  private Map<String, CircuitBreakerState> handleWithMap(final Map<String, CircuitBreaker> breakerMap) {
    final Map<String, CircuitBreakerState> breakerStates = new HashMap<>();

    final List<String> breakersToRemove = new ArrayList<>();
    for (final String identifier : breakerMap.keySet()) {
      final CircuitBreaker breaker = breakerMap.get(identifier);
      if (breaker == null) {
        //Memorize unreferenced breakers which need to be removed later on
        breakersToRemove.add(identifier);
      } else {
        final CircuitBreakerState state =
                new CircuitBreakerState(identifier, breaker.getState());
        breakerStates.put(identifier, state);
      }
    }
    removeUnreferencedBreakers(breakersToRemove);
    return breakerStates;
  }

  private void removeUnreferencedBreakers(final List<String> breakersToRemove) {
    for (final String identifier : breakersToRemove) {
      circuitBreakerRegistry.getConcurrentBreakerMap().remove(identifier);
    }
  }
}
