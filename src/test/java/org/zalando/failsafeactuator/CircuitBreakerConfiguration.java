package org.zalando.failsafeactuator;

import net.jodah.failsafe.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfiguration {

    @Bean
    public CircuitBreaker test() {
        return new CircuitBreaker();
    }

    @Bean
    public CircuitBreaker delay() {
        return new CircuitBreaker();
    }


}
