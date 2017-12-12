package org.zalando.failsafeactuator.sample;

import net.jodah.failsafe.CircuitBreaker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;
import org.zalando.failsafeactuator.endpoint.domain.CircuitBreakerState;
import org.zalando.failsafeactuator.service.FailsafeBreaker;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.zalando.failsafeactuator.sample.SampleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SampleEndpointTest {

  private static final String FAILSAFE_URL = "/failsafe";

  private static final String BREAKER_NAME = "testBreaker";

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  @FailsafeBreaker(BREAKER_NAME)
  private CircuitBreaker breaker;

  @Test
  public void endpointTest() {
    Map<String, CircuitBreakerState> state = fetchCircuitBreaker();
    assertNotEquals(state.get(BREAKER_NAME), null);
    assertEquals(state.get(BREAKER_NAME).getState(), CircuitBreaker.State.CLOSED);

    breaker.open();
    state = fetchCircuitBreaker();
    assertNotEquals(state.get(BREAKER_NAME), null);
    assertEquals(state.get(BREAKER_NAME).getState(), CircuitBreaker.State.OPEN);

    breaker.halfOpen();
    state = fetchCircuitBreaker();
    assertNotEquals(state.get(BREAKER_NAME), null);
    assertEquals(state.get(BREAKER_NAME).getState(), CircuitBreaker.State.HALF_OPEN);
  }

  private Map<String, CircuitBreakerState> fetchCircuitBreaker() {
    ParameterizedTypeReference<Map<String, CircuitBreakerState>> typeReference = new ParameterizedTypeReference<Map<String, CircuitBreakerState>>() { };

    return restTemplate.exchange(FAILSAFE_URL, HttpMethod.GET, HttpEntity.EMPTY, typeReference).getBody();
  }
}
