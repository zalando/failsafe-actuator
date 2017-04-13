[![Build Status](https://travis-ci.org/zalando-incubator/failsafe-actuator.svg?branch=master)](https://travis-ci.org/zalando-incubator/failsafe-actuator)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/failsafe-actuator.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/failsafe-actuator)

# Failsafe actuator

This project provides a simple monitoring interface for spring boot applications which are using the [failsafe](https://github.com/jhalterman/failsafe) library. 
By using this library, the state of your [Circuit Breakers](http://martinfowler.com/bliki/CircuitBreaker.html) (open/closed) will be exposed to your spring applications endpoint.

Having the possibility to monitor the state of a Circuit Breakers is essential for live applications, since you can directly see if one the third parties
your app is relying on, currently is unavailable.

## Dependencies:

* Any build tool using Maven Central, or direct Download
* [Spring Boot](http://projects.spring.io/spring-boot/) Auto Configuration
* Usage of [failsafe](https://github.com/jhalterman/failsafe)

## How to use:

In order to use this endpoint, just add the following dependency to your project:


**Gradle:**
```
compile("org.zalando:failsafe-actuator:0.4.0")
```

**Maven:**
```
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>failsafe-actuator</artifactId>
    <version>0.3.0</version>
</dependency>
```

After you did this, Autowire the `CircuitBreaker` by using 

```
@Autowired
@FailsafeBreaker(value = "WhatABreak")
CircuitBreaker breaker;
```

This will inject a new instance of a circuit breaker to your bean and register it for monitoring.

Example:

```

@Component
public class MyBean {
    
        @Autowired
        @FailsafeBreaker(value = "WhatABreak")
        private CircuitBreaker breaker;
        
}
```

The [endpoint](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html) is reachable via _**http://${yourAddress}/failsafe**_.

The generated output will look like the following:

```
[{"name":"WhatABreak","closed":true,"open":false,"half_open":false}]
```

## How to build:

```
gradle build
```

## Contribution

If you find an issue or have a proposal for improvement/enhancement open an issue in this repositories [Issue Tracker](https://github.com/zalando-incubator/failsafe-actuator/issues).

Pull requests are always welcome!
