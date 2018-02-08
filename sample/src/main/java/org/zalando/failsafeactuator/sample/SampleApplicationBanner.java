package org.zalando.failsafeactuator.sample;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class SampleApplicationBanner implements Banner {

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        String port = environment.getProperty("server.port");
        String banner = "";
        banner += "Failsafe-Actuator sample applicaton is running!\n";
        banner += "\n";
        banner += "See the circuit breaker status:\n";
        banner += "   $ curl http://127.0.0.1:" + port + "/actuator/failsafe\n";
        banner += "Unreliable endpoint that fails every second invocation:\n";
        banner += "   $ curl http://127.0.0.1:" + port + "/unreliable\n";
        banner += "Reliable endpoint using a circuit breaker and fallback:\n";
        banner += "   $ curl http://127.0.0.1:" + port + "/reliable\n";
        banner += "Reliable endpoint using a circuit breaker with 5 second delay and fallback:\n";
        banner += "   $ curl http://127.0.0.1:" + port + "/reliableWithDelay\n";
        out.print(banner);
    }

}
