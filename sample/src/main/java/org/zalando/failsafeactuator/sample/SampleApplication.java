package org.zalando.failsafeactuator.sample;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SampleApplication {

    public static void main(final String[] args) {
        final SpringApplicationBuilder applicationBuilder =
            new SpringApplicationBuilder(SampleApplication.class);
        applicationBuilder.banner(new SampleApplicationBanner()).logStartupInfo(false).run(args);
    }

}