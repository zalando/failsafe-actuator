package de.coderebell.failsafeactuator;

import net.jodah.failsafe.CircuitBreaker;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry which holds a reference to registered circuit breakers.
 *
 * @author mpickhan on 29.06.16.
 */
@Component
public class CircuitBreakerRegistry {

    private static final Map<String, CircuitBreaker> concurrentBreakerMap = new ConcurrentHashMap<String, CircuitBreaker>();

    /**
     * Will put the {@link CircuitBreaker} into the registry. There is no check which avoids overwriting
     * of identifiers. Therefore be sure that your identifiers are unique, or you want to overwrite the
     * current {@link CircuitBreaker} which is registered with this identifier.
     *
     * @param breaker Which should be added
     * @param name    Which is used to identify the CircuitBreaker
     */
    public void registerCircuitBreaker(final CircuitBreaker breaker, final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name for circuitbreaker needs to be set");
        }

        if (breaker == null) {
            throw new IllegalArgumentException("Circuitbreaker to add, can't be null");
        }

        concurrentBreakerMap.put(name, breaker);
    }

    /**
     * Returns the {@link Map} with registered circuit breakers.
     *
     * @return
     */
    public Map<String, CircuitBreaker> getConcurrentBreakerMap() {
        return this.concurrentBreakerMap;
    }

}
