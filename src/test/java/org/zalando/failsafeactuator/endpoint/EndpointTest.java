/**
 * The MIT License (MIT) Copyright (c) 2016 Zalando SE
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.endpoint;

import net.jodah.failsafe.CircuitBreaker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zalando.failsafeactuator.endpoint.domain.CircuitBreakerState;
import org.zalando.failsafeactuator.service.CircuitBreakerFactory;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(value = "classpath:application-test.properties")
@ComponentScan(value = "org.zalando.failsafeactuator")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndpointTest {

  private static final String FAILSAFE_URL = "http://localhost:%d/failsafe";

  private static final String BREAKER_NAME = "testBreaker";

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private CircuitBreakerFactory factory;

  @Autowired
  private CircuitBreakerRegistry registry;

  @Before
  public void setup() {
    registry.getConcurrentBreakerMap().clear();
  }

  @Test
  public void endpointTest() {
    final CircuitBreaker breaker = factory.createCircuitBreaker(BREAKER_NAME);
    CircuitBreakerState state = fetchCircuitBreaker();
    assertTrue(state.isClosed());
    assertEquals(BREAKER_NAME, state.getName());

    breaker.open();
    state = fetchCircuitBreaker();
    assertFalse(state.isClosed());
    assertEquals(BREAKER_NAME, state.getName());
  }

  private CircuitBreakerState fetchCircuitBreaker() {
    final ResponseEntity<CircuitBreakerState[]> result = restTemplate.getForEntity(String.format(FAILSAFE_URL, port), CircuitBreakerState[].class);

    return Arrays.asList(result.getBody()).get(0);
  }
}
