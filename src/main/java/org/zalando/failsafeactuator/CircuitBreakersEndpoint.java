package org.zalando.failsafeactuator;

import net.jodah.failsafe.CircuitBreaker;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.ApplicationContext;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toMap;

@Endpoint(id = "circuit-breakers")
public class CircuitBreakersEndpoint {

    private final ApplicationContext context;

    CircuitBreakersEndpoint(final ApplicationContext context) {
        this.context = context;
    }

    @ReadOperation
    public Map<String, CircuitBreakerView> circuitBreakers() {
        return getCircuitBreakers().entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> toView(entry.getValue())));
    }

    @ReadOperation
    @Nullable
    public CircuitBreakerView circuitBreaker(@Selector final String name) {
        final CircuitBreaker breaker = getCircuitBreaker(name);

        if (breaker == null) {
            return null;
        }

        return toView(breaker);
    }

    @WriteOperation
    @Nullable
    public CircuitBreakerView transitionTo(@Selector final String name, final CircuitBreaker.State state) {
        final CircuitBreaker breaker = getCircuitBreaker(name);

        if (breaker == null) {
            return null;
        }

        transitioner(state).accept(breaker);

        return toView(breaker);
    }

    private Consumer<CircuitBreaker> transitioner(final CircuitBreaker.State state) {
        switch (state) {
            case OPEN:
                return CircuitBreaker::open;
            case HALF_OPEN:
                return CircuitBreaker::halfOpen;
            default:
                // to be able to cover this branch
                return CircuitBreaker::close;
        }
    }

    private CircuitBreakerView toView(final CircuitBreaker breaker) {
        return new CircuitBreakerView(breaker.getState());
    }

    private Map<String, CircuitBreaker> getCircuitBreakers() {
        return context.getBeansOfType(CircuitBreaker.class, false, true);
    }

    @Nullable
    private CircuitBreaker getCircuitBreaker(final String name) {
        try {
            return context.getBean(name, CircuitBreaker.class);
        } catch (final NoSuchBeanDefinitionException e) {
            return null;
        }
    }

}
