package org.zalando.failsafeactuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.failsafeactuator.service.CircuitBreakerFactory;

import javax.annotation.PostConstruct;

@Component
public class TestConfiguration {

    @Autowired
    private CircuitBreakerFactory factory;

    @PostConstruct
    public void init() {
        factory.createCircuitBreaker("Breaker");
    }
}
