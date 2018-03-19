package org.zalando.failsafeactuator.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jodah.failsafe.CircuitBreaker;
import org.junit.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static net.jodah.failsafe.CircuitBreaker.State.CLOSED;
import static net.jodah.failsafe.CircuitBreaker.State.HALF_OPEN;
import static net.jodah.failsafe.CircuitBreaker.State.OPEN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FailsafeEndpointTest {

  private static final String BREAKER_NAME = "TEST";
  private final ObjectMapper mapper = new ObjectMapper();

  private final FailsafeEndpoint endpoint;
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  public FailsafeEndpointTest() {
    circuitBreakerRegistry = new CircuitBreakerRegistry();
    endpoint = new FailsafeEndpoint(circuitBreakerRegistry, new GenericApplicationContext());
  }

  @Test
  public void singleBreakerClosed() throws JsonProcessingException {
    CircuitBreaker test = circuitBreakerRegistry.getOrCreate(BREAKER_NAME);
    test.close();

    String output = mapper.writeValueAsString(endpoint.listCircuitBreakerStates());

    verifyBreaker(output, BREAKER_NAME, CLOSED.toString());
  }

  @Test
  public void singleBreakerOpen() throws JsonProcessingException {
    CircuitBreaker test = circuitBreakerRegistry.getOrCreate(BREAKER_NAME);
    test.open();

    String output = mapper.writeValueAsString(endpoint.listCircuitBreakerStates());

    verifyBreaker(output, BREAKER_NAME, OPEN.toString());
  }

  @Test
  public void singleBreakerHalfOpen() throws JsonProcessingException {
    CircuitBreaker test = circuitBreakerRegistry.getOrCreate(BREAKER_NAME);
    test.halfOpen();

    String output = mapper.writeValueAsString(endpoint.listCircuitBreakerStates());

    verifyBreaker(output, BREAKER_NAME, HALF_OPEN.toString());

  }

  @Test
  public void twoBreakersMixedState() throws JsonProcessingException {
    String breaker_open = "Open";
    String breaker_closed = "Closed";

    CircuitBreaker open = circuitBreakerRegistry.getOrCreate(breaker_open);
    CircuitBreaker closed = circuitBreakerRegistry.getOrCreate(breaker_closed);

    open.open();
    closed.close();

    String output = mapper.writeValueAsString(endpoint.listCircuitBreakerStates());

    verifyBreaker(output, breaker_open, OPEN.toString());
    verifyBreaker(output, breaker_closed, CLOSED.toString());
  }

  private void verifyBreaker(final String input, final String name, final String state) {
    assertThat(input, hasJsonPath("$." + name + ".name", equalTo(name)));
    assertThat(input, hasJsonPath("$." + name + ".state", equalTo(state)));
    assertThat(input, hasJsonPath("$." + name + ".length()", equalTo(2)));
  }
}
