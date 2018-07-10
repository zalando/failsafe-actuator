package org.zalando.actuate.failsafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
final class Container {

    private final Map<String, CircuitBreakerView> circuitBreakers;

}
