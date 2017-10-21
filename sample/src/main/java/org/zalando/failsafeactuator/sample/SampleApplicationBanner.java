package org.zalando.failsafeactuator.sample;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class SampleApplicationBanner implements Banner {

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        out.println("Failsafe-Actuator sample applicaton is running!");
        out.println();
        out.println("See the circuit breaker status:");
        out.println("   $ curl http://127.0.0.1:8080/failsafe");
        out.println("Unreliable endpoint that fails every second invocation:");
        out.println("   $ curl http://127.0.0.1:8080/unreliable");
        out.println("Endpoint with fallback:");
        out.println("   $ curl http://127.0.0.1:8080/reliable");
        out.println("Endpoint with fallback and 5 second circuit breaker delay:");
        out.println("   $ curl http://127.0.0.1:8080/reliableWithDelay");
    }

}
