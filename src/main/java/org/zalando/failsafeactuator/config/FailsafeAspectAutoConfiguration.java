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
package org.zalando.failsafeactuator.config;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.zalando.failsafeactuator.aspect.FailsafeBreakerAspect;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

@EnableAspectJAutoProxy
@Configuration
@Conditional(FailsafeAutoConfiguration.FailsafeCondition.class)
public class FailsafeAspectAutoConfiguration {

  @Bean
  public FailsafeBreakerAspect failsafeBreakerAspect(
      final CircuitBreakerRegistry circuitBreakerRegistry, final CounterService counterService) {
    return new FailsafeBreakerAspect(circuitBreakerRegistry, counterService);
  }

  @Bean
  public Advisor failsafeBreakerPointcutAdvisor(final FailsafeBreakerAspect failsafeBreakerAspect) {
    final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    pointcut.setExpression("@annotation(org.zalando.failsafeactuator.aspect.Failsafe)");
    return new DefaultPointcutAdvisor(pointcut, failsafeBreakerAspect);
  }
}
