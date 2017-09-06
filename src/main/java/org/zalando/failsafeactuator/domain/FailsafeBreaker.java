package org.zalando.failsafeactuator.domain;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import lombok.Data;
import net.jodah.failsafe.CircuitBreaker;

@Data
public class FailsafeBreaker extends CircuitBreaker {

    private final Meter failureMeter;
    private final Meter successMeter;
    private final Meter overallMeter;
    private String identifier;

    public FailsafeBreaker(final MetricRegistry metricRegistry, final String identifier) {
        this.identifier = identifier;
        this.failureMeter = metricRegistry.meter(this.identifier+".failures");
        this.successMeter = metricRegistry.meter(this.identifier+".success");
        this.overallMeter = metricRegistry.meter(this.identifier+".overall");
    }

    @Override
    public void recordResult(Object result) {
        this.overallMeter.mark();
        super.recordResult(result);
    }

    @Override
    public void recordSuccess() {
        this.successMeter.mark();
        super.recordSuccess();
    }

    @Override
    public void recordFailure(Throwable failure) {
        this.failureMeter.mark();
        super.recordFailure(failure);
    }
}
