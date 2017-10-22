package org.zalando.failsafeactuator.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

public class DropwizardMetric implements PublicMetrics {

  public static final String SUCCESS = ".success";
  public static final String FAILURE = ".failure";

  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final MetricRegistry metricRegistry;

  public DropwizardMetric(
      final MetricRegistry metricRegistry, final CircuitBreakerRegistry circuitBreakerRegistry) {
    this.metricRegistry = metricRegistry;
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
    final Counter counter = metricRegistry.counter(key);
    final int count = new BigDecimal(counter.getCount()).intValue();
    // Reset counter
    counter.dec(count);
    return new Metric<>(key, count, Calendar.getInstance().getTime());
  }
}
