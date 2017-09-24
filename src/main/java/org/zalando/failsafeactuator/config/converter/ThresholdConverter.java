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
      default:
        throw new IllegalArgumentException("configuration must have 1 or 2 parameters separate by ','");
    }
  }
}
