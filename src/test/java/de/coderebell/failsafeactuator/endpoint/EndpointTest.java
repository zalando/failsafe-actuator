package de.coderebell.failsafeactuator.endpoint;

import de.coderebell.failsafeactuator.CircuitBreakerRegistry;
import de.coderebell.failsafeactuator.FailsafeSampleApp;
import de.coderebell.failsafeactuator.endpoint.domain.CircuitBreakerState;
import net.jodah.failsafe.CircuitBreaker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(value = "classpath:application-test.properties")
@SpringApplicationConfiguration(classes = FailsafeSampleApp.class)
@WebIntegrationTest
public class EndpointTest {

    private static final String FAILSAFE_URL = "http://localhost:8080/failsafe";

    private static final String BREAKER_NAME = "testBreaker";

    private RestTemplate restTemplate = new TestRestTemplate();


    @Autowired
    private CircuitBreakerRegistry registry;

    @Test
    public void endpointTest() {
        final CircuitBreaker breaker = new CircuitBreaker();
        registry.registerCircuitBreaker(breaker, BREAKER_NAME);

        CircuitBreakerState state = fetchCircuitBreaker();
        System.out.println(state.toString());
        System.out.println(breaker.toString());
        assertTrue(state.isClosed());
        assertEquals(BREAKER_NAME, state.getName());

        breaker.open();
        state = fetchCircuitBreaker();
        System.out.println(state.toString());
        System.out.println(breaker.toString());
        assertFalse(state.isClosed());
        assertEquals(BREAKER_NAME, state.getName());
    }


    private CircuitBreakerState fetchCircuitBreaker() {
        final ResponseEntity<CircuitBreakerState[]> result = restTemplate.getForEntity(FAILSAFE_URL, CircuitBreakerState[].class);

        return Arrays.asList(result.getBody()).get(0);
    }
}
