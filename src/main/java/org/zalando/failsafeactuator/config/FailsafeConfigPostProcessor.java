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

@Slf4j
public class FailsafeConfigPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {
  private ConfigurableEnvironment environment;

  private FailsafeConfig failsafeConfig;

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
    FailsafeConfig failsafeConfig = getFailsafeConfig();
    failsafeConfig.populateBreakersWithDefaultsIfNecessary();
    log.debug("{}", failsafeConfig);
  }

  @SneakyThrows
  FailsafeConfig getFailsafeConfig() {
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

  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = (ConfigurableEnvironment) environment;
  }
}
