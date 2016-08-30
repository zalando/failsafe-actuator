package org.zalando.failsafeactuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "org.zalando.failsafeactuator")
public class PostConstructSampleApp {

    public static void main(final String[] args) {
        SpringApplication.run(PostConstructSampleApp.class, args);
    }

}
