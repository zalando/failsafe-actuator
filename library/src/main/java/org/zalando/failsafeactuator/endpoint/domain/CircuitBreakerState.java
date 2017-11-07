package org.zalando.failsafeactuator.endpoint.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents the state and identifier of an {@link net.jodah.failsafe.CircuitBreaker}.
 *
 * @author mpickhan on 29.06.16.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CircuitBreakerState {

  private String name;

  private boolean closed;

  private boolean open;

  private boolean halfOpen;
}
