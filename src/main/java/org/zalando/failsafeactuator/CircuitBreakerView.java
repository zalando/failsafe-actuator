package org.zalando.failsafeactuator;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Value;
import net.jodah.failsafe.CircuitBreaker;

@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
final class CircuitBreakerView {

    private final CircuitBreaker.State state;

}
