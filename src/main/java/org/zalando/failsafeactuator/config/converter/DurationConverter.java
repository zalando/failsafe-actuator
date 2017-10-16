/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Zalando SE
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.config.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
public class DurationConverter implements Converter<String, Duration> {

  private static final int TIME_UNIT = 1;
  private static final int AMOUNT = 0;

  @Override
  public Duration convert(String s) {
    String[] sections = s.split(" ");
    if (sections.length == 2) {
      String amount = sections[AMOUNT].trim();
      String timeUnit = sections[TIME_UNIT].trim();
      return Duration.of(Long.valueOf(amount), ChronoUnit.valueOf(timeUnit.toUpperCase()));
    }
    return null;
  }
}
