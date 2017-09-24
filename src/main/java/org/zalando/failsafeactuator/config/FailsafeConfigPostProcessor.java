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
package org.zalando.failsafeactuator.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.zalando.failsafeactuator.config.converter.DurationConverter;
import org.zalando.failsafeactuator.config.converter.ThresholdConverter;
import org.zalando.failsafeactuator.config.model.FailsafeConfig;
import org.zalando.failsafeactuator.service.CircuitBreakerRegistry;

@Slf4j
public class FailsafeConfigPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
  private ConfigurableEnvironment environment;

  private FailsafeConfig failsafeConfig;

  private CircuitBreakerRegistry circuitBreakerRegistry;

  public FailsafeConfigPostProcessor(CircuitBreakerRegistry circuitBreakerRegistry) {
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
    FailsafeConfig failsafeConfig = getFailsafeConfig();
    failsafeConfig.populateBreakersWithDefaultsIfNecessary();
    circuitBreakerRegistry.setFailsafeConfig(failsafeConfig);
    log.debug("{}", failsafeConfig);
  }

  @SneakyThrows
  private FailsafeConfig getFailsafeConfig() {
    if (failsafeConfig == null) {
      final PropertiesConfigurationFactory<FailsafeConfig> factory =
        new PropertiesConfigurationFactory<>(FailsafeConfig.class);

      factory.setTargetName("failsafe");
      factory.setPropertySources(environment.getPropertySources());
      ConfigurableConversionService conversionService = environment.getConversionService();
      conversionService.addConverter(new DurationConverter());
      conversionService.addConverter(new ThresholdConverter());
      factory.setConversionService(conversionService);

      failsafeConfig = factory.getObject();
    }
    return failsafeConfig;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    // Not necessary here
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = (ConfigurableEnvironment) environment;
  }
}
