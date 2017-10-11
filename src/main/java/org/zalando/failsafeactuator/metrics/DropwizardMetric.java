package org.zalando.failsafeactuator.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import net.jodah.failsafe.Failsafe;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

public class DropwizardMetric implements PublicMetrics {

  private static final String SUCCESS = ".success";
  private static final String FAILURE = ".failure";

  private final CircuitBreakerRegistry circuitBreakerRegistry;

  private final MetricRegistry metricRegistry;
  private final Map<String, List<Meter>> metricMap;

  public DropwizardMetric(
      final MetricRegistry metricRegistry, final CircuitBreakerRegistry circuitBreakerRegistry) {
    this.metricRegistry = metricRegistry;
    metricMap = new ConcurrentHashMap<>();
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @PostConstruct
  public void init() {
    for (final String identifier : circuitBreakerRegistry.getConcurrentBreakerMap().keySet()) {
      final Meter successMeter = metricRegistry.meter(identifier + SUCCESS);
      final Meter failureMeter = metricRegistry.meter(identifier + FAILURE);
      metricMap.put(identifier, Arrays.asList(successMeter, failureMeter));
      Failsafe.with(circuitBreakerRegistry.getConcurrentBreakerMap().get(identifier))
          .with(new DropwizardListener(failureMeter, successMeter));
    }
  }

  @Override
  public Collection<Metric<?>> metrics() {
    final List metricList = new ArrayList();
    for (final String key : metricMap.keySet()) {
      for (final Meter meter : metricMap.get(key)) {
        final Metric metric =
            new Metric<>(
                key, new BigDecimal(meter.getCount()).intValue(), Calendar.getInstance().getTime());
        metricList.add(metric);
      }
    }
    return metricList;
  }
}
