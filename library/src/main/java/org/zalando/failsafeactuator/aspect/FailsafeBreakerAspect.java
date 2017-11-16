/**
 * The MIT License (MIT) Copyright (c) 2016 Zalando SE
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.zalando.failsafeactuator.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeException;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.boot.actuate.metrics.CounterService;
import org.zalando.failsafeactuator.metrics.CountingListener;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

@Slf4j
@RequiredArgsConstructor
public class FailsafeBreakerAspect implements MethodInterceptor {

  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final CounterService counterService;

  private ScheduledThreadPoolExecutor executor;

  @Override
  public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
    final Method method = methodInvocation.getMethod();

    final org.zalando.failsafeactuator.aspect.Failsafe breaker =
        method.getAnnotation(org.zalando.failsafeactuator.aspect.Failsafe.class);


    final CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate(breaker.value());
    try {

      if(method.getReturnType().isInstance(Future.class)) {
        if(executor == null) {
          //TODO: introduce property to resolve
          executor = new ScheduledThreadPoolExecutor(100);
        }
        return Failsafe.with(circuitBreaker)
                .with(new CountingListener<>(counterService, breaker.value()))
                .with(executor).future((Callable<CompletableFuture<Object>>) methodInvocation.proceed());
      } else {
        return Failsafe.with(circuitBreaker)
                .with(new CountingListener<>(counterService, breaker.value()))
                .get(() -> {
                          try {
                            return methodInvocation.proceed();
                          } catch (final Throwable throwable) {
                            throw new FailsafeException(throwable);
                          }
                        });
      }
    } catch (final RuntimeException e) {
      final String fallbackMethodName = breaker.fallbackMethod();
      if (!fallbackMethodName.isEmpty()) {
        try {
          return executeFallback(methodInvocation, fallbackMethodName);
        } catch (IllegalAccessException
            | IllegalArgumentException
            | InvocationTargetException fallbackException) {
          throw fallbackException;
        } catch (final Exception fallbackException) {
          log.debug("Fallback throws exception", fallbackException);
        }
      }

      if (e.getCause() != null) {
        throw e.getCause();
      }
      throw e;
    }
  }

  private Object executeFallback(
      final MethodInvocation methodInvocation, final String fallbackMethodName) throws Throwable {
    final Method method = methodInvocation.getMethod();
    final Object targetObject = methodInvocation.getThis();
    final Method fallbackMethod = lookupMethod(method, fallbackMethodName, targetObject);
    try {
      return fallbackMethod.invoke(targetObject, methodInvocation.getArguments());
    } catch (final InvocationTargetException e) {
      throw e.getCause();
    }
  }

  private Method lookupMethod(
      final Method method, final String fallbackMethodName, final Object targetObject)
      throws NoSuchMethodException {
    final Class<?> methodClass = targetObject.getClass();
    return methodClass.getMethod(fallbackMethodName, method.getParameterTypes());
  }
}
