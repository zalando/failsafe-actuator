package org.zalando.failsafeactuator.config.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DurationConverter implements Converter<String, Duration> {

  private static final int TIME_UNIT = 1;
  private static final int AMOUNT = 0;

  @Override
  public Duration convert(String s) {
    String[] sections = s.split(",");
    if (sections.length == 2) {
      String amount = sections[AMOUNT].trim();
      String timeUnit = sections[TIME_UNIT].trim();
      return Duration.of(Long.valueOf(amount), ChronoUnit.valueOf(timeUnit));
    }
    return null;
  }
}
