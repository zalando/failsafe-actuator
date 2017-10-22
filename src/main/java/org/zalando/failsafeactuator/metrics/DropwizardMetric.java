package org.zalando.failsafeactuator.metrics;

import static org.zalando.failsafeactuator.metrics.DropwizardListener.FAILURE;
import static org.zalando.failsafeactuator.metrics.DropwizardListener.SUCCESS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.reader.MetricReader;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

public class DropwizardMetric implements PublicMetrics {

  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final MetricReader metricReader;

  public DropwizardMetric(
      final MetricReader metricReader, final CircuitBreakerRegistry circuitBreakerRegistry) {
    this.metricReader = metricReader;
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @Override
  public Collection<Metric<?>> metrics() {
    final List metricList = new ArrayList();
    for (final String key : circuitBreakerRegistry.getConcurrentBreakerMap().keySet()) {
      metricList.add(createMetric(key + SUCCESS));
      metricList.add(createMetric(key + FAILURE));
    }
    return metricList;
  }

  private Metric createMetric(final String key) {
    return metricReader.findOne(key);
  }
}
