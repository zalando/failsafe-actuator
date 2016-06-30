package de.coderebell.failsafeactuator.endpoint;

import de.coderebell.failsafeactuator.CircuitBreakerRegistry;
import de.coderebell.failsafeactuator.endpoint.domain.CircuitBreakerState;
import net.jodah.failsafe.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link AbstractEndpoint} for Failsafe purposes.
 * <p>
 * It will return all names of registered {@link CircuitBreaker}'s and their state
 * as JSON.
 *
 * @author mpickhan on 29.06.16.
 */
@Component
public class FailsafeEndpoint extends AbstractEndpoint<List<CircuitBreakerState>> {

    private static final String ENDPOINT_ID = "failsafe";
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    public FailsafeEndpoint(final CircuitBreakerRegistry circuitBreakerRegistry) {
        super(ENDPOINT_ID);
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public List<CircuitBreakerState> invoke() {
        System.out.println("INVOKE!");
        final Map<String, CircuitBreaker> breakerMap = this.circuitBreakerRegistry.getConcurrentBreakerMap();
        final List<CircuitBreakerState> breakerStates = new ArrayList<CircuitBreakerState>();

        final List<String> breakersToRemove = new ArrayList<String>();
        for (final String identifier : breakerMap.keySet()) {
            final CircuitBreaker breaker = breakerMap.get(identifier);
            if (breaker == null) {
                //Memorize unreferenced breakers which need to be removed later on
                breakersToRemove.add(identifier);
            } else {
                System.out.println("Breaker in invoke" + breaker.toString());
                final CircuitBreakerState state = new CircuitBreakerState(identifier, breaker.isClosed());
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
