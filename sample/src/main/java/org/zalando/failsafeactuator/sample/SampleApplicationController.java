package org.zalando.failsafeactuator.sample;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@RestController
public class SampleApplicationController {
    private boolean shouldFail = false;

    @Autowired
    @org.zalando.failsafeactuator.aspect.Failsafe(value = "testBreaker")
    private CircuitBreaker breaker;

    @Autowired
    @org.zalando.failsafeactuator.aspect.Failsafe(value = "delayBreaker")
    private CircuitBreaker delayBreaker;

    @PostConstruct
    private void init() {
        delayBreaker.withDelay(5, TimeUnit.SECONDS);
    }

    @RequestMapping("/unreliable")
    public String sayHelloWorldUnreliable() {
        return getMessage();
    }

    @RequestMapping("/reliable")
    public String sayHelloWorldReliable() {
        return Failsafe.with(breaker).withFallback("Service unavailable").get(this::getMessage);
    }

    @RequestMapping("/reliableWithDelay")
    public String sayHelloWorldReliableWithDelay() {
        return Failsafe.with(delayBreaker).withFallback("Service unavailable").get(this::getMessage);
    }

    private String getMessage() {
        if (shouldFail) {
            shouldFail = false;
            throw new RuntimeException("Every second try fails");
        } else {
            shouldFail = true;
            return "Hello world!";
        }
    }
}