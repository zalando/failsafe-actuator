/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Zalando SE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.failsafeactuator.service.FailsafeBreaker;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class FailsafeSampleApp {

    @RestController
    public class HelloWorldController {
        private boolean shouldFail = false;

        @Autowired
        @FailsafeBreaker(value = "testBreaker")
        private CircuitBreaker breaker;

        @Autowired
        @FailsafeBreaker(value = "delayBreaker")
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

    public static void main(final String[] args) {
        SpringApplication.run(FailsafeSampleApp.class, args);
    }

}