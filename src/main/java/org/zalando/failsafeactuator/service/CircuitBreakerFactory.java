package org.zalando.failsafeactuator.service;

import org.springframework.beans.factory.FactoryBean;

import net.jodah.failsafe.CircuitBreaker;

public class CircuitBreakerFactory implements FactoryBean<CircuitBreaker> {

	@Override
	public CircuitBreaker getObject() throws Exception {
		return new CircuitBreaker();
	}

	@Override
	public Class<?> getObjectType() {
		return CircuitBreaker.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
}
