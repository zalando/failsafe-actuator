[![Codacy Badge](https://api.codacy.com/project/badge/Grade/97639870e76546cab6fd2597c583c0b1)](https://www.codacy.com/app/MALPI/failsafe-actuator?utm_source=github.com&utm_medium=referral&utm_content=zalando-incubator/failsafe-actuator&utm_campaign=badger)
[![Build Status](https://travis-ci.org/zalando-incubator/failsafe-actuator.svg?branch=master)](https://travis-ci.org/zalando-incubator/failsafe-actuator)
[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/failsafe-actuator.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/failsafe-actuator)

# Failsafe Actuator

**Failsafe Actuator** is a Java library that provides a simple monitoring interface for [Spring Boot](https://projects.spring.io/spring-boot/) 
applications that use the [Failsafe](https://github.com/jhalterman/failsafe) library. 
Using Failsafe Actuator will readily expose the state of your [Circuit Breakers](http://martinfowler.com/bliki/CircuitBreaker.html) (closed, open, half-open) 
to your Spring Actuator endpoint without additional effort. 
Use the `@FailsafeBreaker` annotation to inject a circuit breaker wherever you need one. 

## Core Technical Concepts/Inspiration

Failsafe Actuator supports Spring's dependency injection to make it easier to use *Failsafe*. 
It allows you to monitor the state of your Circuit Breakers so that, whenever a third party that your app relies upon 
suddenly becomes unavailable, you can discover it immediately and take action. This is essential for applications used in production.

## Development Status/Project Roadmap
This library is currently under development and used in production at [Zalando](https://jobs.zalando.com/tech/). 
It will offer additional features in the future, such as metrics-gathering for:
* failed requests past 1m/5m/15m
* success requests past 1m/5m/15m
* overall requests past 1m/5m/15m
* easy configuration of Circuit Breaker and Policies

Find more details about our development plans in the [Issues Tracker](https://github.com/zalando-incubator/failsafe-actuator/issues). 

We're [looking for contributors](https://github.com/zalando-incubator/failsafe-actuator/blob/master/CONTRIBUTIONS.md), 
so if you find an interesting "Help Wanted" issue then please drop us a line in the related issue to claim it and begin working.

Unless you explicitly state otherwise in advance, any non trivial contribution intentionally submitted for inclusion in this project by you to the steward of this repository (Zalando SE, Berlin) shall be under the terms and conditions of the MIT License, without any additional copyright information, terms or conditions.

## Getting Started

### Dependencies/Requirements
* Java 8
* [Spring Boot 2](http://projects.spring.io/spring-boot/) 
* [Failsafe](https://github.com/jhalterman/failsafe)

### Running/Using

To use Failsafe Actuator, add the following dependency to your project:

**Gradle:**
```groovy
compile("org.zalando:failsafe-actuator:0.4.1")
```

**Maven:**
```xml
<dependency>
    <groupId>org.zalando</groupId>
    <artifactId>failsafe-actuator</artifactId>
    <version>0.4.1</version>
</dependency>
```

Then autowire the `CircuitBreaker` by using:

```java
@Autowired
@FailsafeBreaker("WhatABreak")
CircuitBreaker breaker;
```

This will inject a new instance of a circuit breaker to your bean and register it for monitoring. Example:

```java
@Component
public class MyBean {
    
        @Autowired
        @FailsafeBreaker(value = "WhatABreak")
        private CircuitBreaker breaker;
        
}
```

The [endpoint](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html) is reachable via _**http://${yourAddress}/application/failsafe**_.

The generated output will look like this:

```json
[{"name":"WhatABreak","closed":true,"open":false,"half_open":false}]
```

### How to Build on Your Own

```bash
gradle build
```

### Running the sample application

```bash
cd sample
mvn spring-boot:run
```

## License

This code is released under the MIT license. See [License](LICENSE).
