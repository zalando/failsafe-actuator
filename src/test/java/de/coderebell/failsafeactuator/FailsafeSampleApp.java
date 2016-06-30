package de.coderebell.failsafeactuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by mpickhan on 29.06.16.
 */
@SpringBootApplication
@ComponentScan(value = "de.coderebell.failsafeactuator")
public class FailsafeSampleApp {

    public static void main(final String[] args) {
        SpringApplication.run(FailsafeSampleApp.class, args);
    }
}
