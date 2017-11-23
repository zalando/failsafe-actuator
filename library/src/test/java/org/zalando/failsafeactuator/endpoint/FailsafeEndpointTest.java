package org.zalando.failsafeactuator.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.jodah.failsafe.CircuitBreaker;
import org.junit.Test;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FailsafeEndpointTest {

  private final ObjectMapper mapper = new ObjectMapper();

  private final FailsafeEndpoint endpoint;
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  public FailsafeEndpointTest() {
    circuitBreakerRegistry = new CircuitBreakerRegistry();
    endpoint = new FailsafeEndpoint(circuitBreakerRegistry);
  }

  @Test
  public void singleBreakerClosed() throws JsonProcessingException {
    CircuitBreaker test = circuitBreakerRegistry.getOrCreate("Test");
    test.close();

    String output = mapper.writeValueAsString(endpoint.invoke());

    verifyBreaker(output, "Test", "CLOSED");
  }

  @Test
  public void singleBreakerOpen() throws JsonProcessingException {
    CircuitBreaker test = circuitBreakerRegistry.getOrCreate("Test");
    test.open();

    String output = mapper.writeValueAsString(endpoint.invoke());

    verifyBreaker(output, "Test", "OPEN");
  }

  @Test
  public void singleBreakerHalfOpen() throws JsonProcessingException {
    CircuitBreaker test = circuitBreakerRegistry.getOrCreate("Test");
    test.halfOpen();

    String output = mapper.writeValueAsString(endpoint.invoke());

    verifyBreaker(output, "Test", "HALF_OPEN");

  }

  @Test
  public void twoBreakersMixedState() throws JsonProcessingException {
    CircuitBreaker open = circuitBreakerRegistry.getOrCreate("Open");
    open.open();

    CircuitBreaker closed = circuitBreakerRegistry.getOrCreate("Closed");
    closed.close();

    String output = mapper.writeValueAsString(endpoint.invoke());

    verifyBreaker(output, "Open", "OPEN");
    verifyBreaker(output, "Closed", "CLOSED");
  }

  private void verifyBreaker(final String input, final String name, final String state) {
    assertThat(input, hasJsonPath("$." + name + ".name", equalTo(name)));
    assertThat(input, hasJsonPath("$." + name + ".state", equalTo(state)));
    assertThat(input, hasJsonPath("$." + name + ".length()", equalTo(2)));
  }
}
