package org.zalando.failsafeactuator.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.zalando.failsafeactuator.config.model.FailsafeConfig;

import static java.lang.Integer.parseInt;

public class ThresholdConverter implements Converter<String, FailsafeConfig.Threshold> {
  @Override
  public FailsafeConfig.Threshold convert(String s) {
    String[] sections = s.split(",");
    switch (sections.length) {
      case 1:
        String threshold = sections[0].trim();
        return new FailsafeConfig.Threshold(Integer.valueOf(threshold));
      case 2:
        threshold = sections[0].trim();
        String executions = sections[1].trim();
        return new FailsafeConfig.Threshold(parseInt(threshold, parseInt(executions)));
    }
    return null;
  }
}
