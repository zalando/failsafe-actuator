package de.coderebell.failsafeactuator;

import net.jodah.failsafe.CircuitBreaker;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    public void putBreakerOkTest() {
        final CircuitBreaker breakerUt = new CircuitBreaker();
        registry.registerCircuitBreaker(breakerUt, ID);

        assertTrue(registry.getConcurrentBreakerMap().containsKey(ID));
        final CircuitBreaker breaker = registry.getConcurrentBreakerMap().get(ID);
        assertNotNull(breaker);
        assertEquals(breakerUt, breaker);
    }
}