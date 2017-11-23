/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Zalando SE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.endpoint;

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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = org.zalando.failsafeactuator.sample.SampleApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndpointTest {

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
    assertThat(state.get(BREAKER_NAME), is(not(nullValue())));
    assertThat(state.get(BREAKER_NAME).getState(), equalTo(CircuitBreaker.State.CLOSED));

    breaker.open();
    state = fetchCircuitBreaker();
    assertThat(state.get(BREAKER_NAME), is(not(nullValue())));
    assertThat(state.get(BREAKER_NAME).getState(), equalTo(CircuitBreaker.State.OPEN));

    breaker.halfOpen();
    state = fetchCircuitBreaker();
    assertThat(state.get(BREAKER_NAME), is(not(nullValue())));
    assertThat(state.get(BREAKER_NAME).getState(), equalTo(CircuitBreaker.State.HALF_OPEN));
  }

  private Map<String, CircuitBreakerState> fetchCircuitBreaker() {
    ParameterizedTypeReference<Map<String, CircuitBreakerState>> typeReference = new ParameterizedTypeReference<Map<String, CircuitBreakerState>>() { };

    return restTemplate.exchange(FAILSAFE_URL, HttpMethod.GET, HttpEntity.EMPTY, typeReference).getBody();
  }
}
