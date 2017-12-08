package org.zalando.failsafeactuator.sample;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SampleApplication {

  public static void main(final String[] args) {
    new SpringApplicationBuilder(SampleApplication.class)
        .logStartupInfo(false)
        .run(args);
  }

}