package org.zalando.actuate.failsafe;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@ResponseBody
public class SampleController {

    private final AtomicBoolean fail = new AtomicBoolean();

    @Autowired
    private CircuitBreaker test;

    @Autowired
    private CircuitBreaker delay;

    @PostConstruct
    private void init() {
        delay.withDelay(5, TimeUnit.SECONDS);
    }

    @RequestMapping("/unreliable")
    public String sayHelloWorldUnreliable() {
        return getMessage();
    }

    @RequestMapping("/reliable")
    public String sayHelloWorldReliable() {
        return Failsafe.with(test).withFallback("Service unavailable").get(this::getMessage);
    }

    @RequestMapping("/reliableWithDelay")
    public String sayHelloWorldReliableWithDelay() {
        return Failsafe.with(delay).withFallback("Service unavailable").get(this::getMessage);
    }

    private String getMessage() {
        if (fail.compareAndSet(true, false)) {
            throw new RuntimeException("Every second try fails");
        } else if (fail.compareAndSet(false, true)) {
            return "Hello world!";
        } else {
            throw new IllegalStateException();
        }
    }
}
