package org.zalando.failsafeactuator.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import net.jodah.failsafe.CircuitBreaker;
import org.springframework.util.Assert;
import org.zalando.failsafeactuator.endpoint.FailsafeEndpoint;

/**
 * Registry which holds a reference to registered circuit breakers.
 *
 * @author mpickhan on 29.06.16.
 */
public class CircuitBreakerRegistry {

  private static final String ALREADY_REGISTERED_ERROR = "There was a Circuit-Breaker registered already with name : %s ";

  private final Map<String, CircuitBreaker> concurrentBreakerMap = new ConcurrentHashMap<>();

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
    return concurrentBreakerMap;
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
   * @deprecated This method shouldn't be used anymore. Failsafe-Actuator relies on Spring Context in the future
   * therefore you should instantiate your Circuit Breaker by creating a {@link org.springframework.context.annotation.Bean}.
   * This method will be dropped in future versions.
   * @param identifier which will be shown in the output of the {@link FailsafeEndpoint}.
   * @return an already registered or a new Instance of a {@link CircuitBreaker}
   */
  @Deprecated
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
