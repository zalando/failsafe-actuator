package org.zalando.failsafeactuator.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jodah.failsafe.CircuitBreaker;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.zalando.failsafeactuator.endpoint.domain.CircuitBreakerState;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

/**
 * Implementation of {@link AbstractEndpoint} for Failsafe purposes.
 *
 * <p>It will return all names of registered {@link CircuitBreaker}'s and their state as JSON.
 *
 * @author mpickhan on 29.06.16.
 */
@ConfigurationProperties(prefix = "endpoints.failsafe")
public class FailsafeEndpoint extends AbstractEndpoint<Map<String, CircuitBreakerState>> {

  private static final String ENDPOINT_ID = "failsafe";
  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final ApplicationContext context;

  public FailsafeEndpoint(final CircuitBreakerRegistry circuitBreakerRegistry, final ApplicationContext context) {
    super(ENDPOINT_ID);
    this.circuitBreakerRegistry = circuitBreakerRegistry;
    this.context = context;
  }

  @Override
  public Map<String, CircuitBreakerState> invoke() {
    final Map<String, CircuitBreaker> breakerMap = circuitBreakerRegistry.getConcurrentBreakerMap();

    if(!breakerMap.isEmpty()) {
      return handleWithMap(breakerMap);
    } else {
      return handleWithApplicationContext();
    }

  }

  private List<CircuitBreakerState> handleWithApplicationContext() {
    List<CircuitBreakerState> resultList = new ArrayList<>();

    final String[] beanNamesForType = context.getBeanNamesForType(CircuitBreaker.class);

    if(beanNamesForType == null) {
      return resultList;
    }

    for(int i = 0; i < beanNamesForType.length; i++) {
      String identifier = beanNamesForType[i];
      CircuitBreaker breaker = context.getBean(beanNamesForType[i], CircuitBreaker.class);
      if(breaker != null) {
        final CircuitBreakerState state =
                new CircuitBreakerState(identifier, breaker.isClosed(), breaker.isOpen(), breaker.getState().equals(CircuitBreaker.State.HALF_OPEN));
        resultList.add(state);
      }
    }

    return resultList;
  }

  private List<CircuitBreakerState> handleWithMap(final Map<String, CircuitBreaker> breakerMap) {
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
