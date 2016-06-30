[![Build Status](https://travis-ci.org/MALPI/failsafe-actuator.svg?branch=master)](https://travis-ci.org/MALPI/failsafe-actuator)

# Failsafe actuator

This project should provide a simple monitoring interface for spring boot applications which are using [failsafe](https://github.com/jhalterman/failsafe) . 

By using this actuator, the state of your breakpoints (open/closed) will be exposed to your applications monitoring endpoint.

The [endpoint](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html) is reachable via _**http://${yourAddress}/failsafe**_.

The generated output will look like the following:

```
CircuitBreakerState{name='testBreaker1', isClosed=true}
CircuitBreakerState{name='testBreaker2', isClosed=false}
```

## How to use:

In order to use this endpoint, just add the following dependency to your project:

```
TODO ADD dependency here
```

After you did this, Autowire the `CircuitBreakerFactory` to your Bean and call the method `createCircuitBreaker` in order to create a new **Circuit Breaker**.

Example:

```

@Component
public class MyBean {
    
        @Autowired
        private CircuitBreakerFactory factory;
        
        @Postconstruct
        public void init() {
            //The created Circuit breaker is automatically registered at the endpoint with the given name
            CircuitBreaker breaker = factory.createCircuitBreaker("SpringBreak");
        }
}
```

That's all.
## How to build:

```
gradle build
```

## Contribution

If you find an issue or have a proposal for improvement/enhancement open an issue.

Pull requests are always welcome!




