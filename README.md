[![Build Status](https://travis-ci.org/zalando-incubator/failsafe-actuator.svg?branch=master)](https://travis-ci.org/zalando-incubator/failsafe-actuator)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/failsafe-actuator.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/failsafe-actuator)

# Failsafe Actuator

**Failsafe Actuator** provides a simple monitoring interface for [Spring Boot](https://projects.spring.io/spring-boot/) applications which are using the [Failsafe](https://github.com/jhalterman/failsafe) library. 
By using this library, the state of your [Circuit Breakers](http://martinfowler.com/bliki/CircuitBreaker.html) (closed, open, half-open) will be exposed to your Spring Actuator endpoint.

Just make use of the `@FailsafeBreaker` annotation, to inject a Circuit Breaker where you need it. The state information of the Circuit Breaker will be exposed out of the box and without any additional efforts.


The library will be enhanced with furhter features, a description of them can be found in the issue tracker. Still it's already used in production environments by Zalando.

## Core Technical Concepts/Inspiration

By enabling the support of Springs dependency injection, the usage of *Failsafe* is even easier. 

Having the possibility to monitor the state of a Circuit Breakers is essential for applications which are used in production, since you can directly see if one of the third parties your app is relying on, currently is unavailable. This enables you and your teams to act accordingly and take the right decisions quick.

## Roadmap

* Gather Metrics like:
    * failed requests past 1m/5m/15m
    * success requests past 1m/5m/15m
    * overall requests past 1m/5m/15m

## Getting Started

In order to use Failsafe Actuator, just add the following dependency to your project:


**Gradle:**
```
compile("org.zalando:failsafe-actuator:0.4.0")
```

**Maven:**
```
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>failsafe-actuator</artifactId>
    <version>0.4.0</version>
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

## Dependencies
* Java 8
* [Spring Boot](http://projects.spring.io/spring-boot/) 
* Usage of [failsafe](https://github.com/jhalterman/failsafe)


## How to build on your own

```
gradle build
```

## Contribution

If you find an issue or have a proposal for improvement/enhancement open an issue in this repositories [Issue Tracker](https://github.com/zalando-incubator/failsafe-actuator/issues).

Pull requests are always welcome!

## License

This code is released under the MIT license.