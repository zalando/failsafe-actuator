package org.zalando.failsafeactuator;

import net.jodah.failsafe.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;

@Configuration
public class FailsafeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public CircuitBreakersEndpoint circuitBreakersEndpoint(
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
            @Autowired(required = false) @Nullable final Map<String, CircuitBreaker> breakers) {
        return new CircuitBreakersEndpoint(breakers == null ? Collections.emptyMap() : breakers);
    }

}
