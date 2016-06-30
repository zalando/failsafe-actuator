package de.coderebell.failsafeactuator.endpoint.domain;

/**
 * Represents the state and identifier of an {@link net.jodah.failsafe.CircuitBreaker}.
 *
 * @author mpickhan on 29.06.16.
 */
public class CircuitBreakerState {

    private String name;

    private boolean isClosed;

    public CircuitBreakerState() {

    }

    public CircuitBreakerState(final String name, final boolean isClosed) {
        this.name = name;
        this.isClosed = isClosed;
    }

    public boolean isClosed() {
        return this.isClosed;
    }

    public void setClosed(final boolean closed) {
        isClosed = closed;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CircuitBreakerState{" +
                "name='" + name + '\'' +
                ", isClosed=" + isClosed +
                '}';
    }
}
