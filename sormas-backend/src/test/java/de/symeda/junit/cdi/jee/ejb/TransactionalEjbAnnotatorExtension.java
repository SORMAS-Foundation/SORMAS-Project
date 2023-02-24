/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.junit.cdi.jee.ejb;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;

import de.hilling.junit.cdi.annotations.BypassTestInterceptor;

/**
 * CDI {@link javax.enterprise.inject.spi.Extension} to annotate EJB classes with an InterceptorBinding annotation.
 * Needed to emulate bean-container-like transaction handling.
 */
@BypassTestInterceptor
public class TransactionalEjbAnnotatorExtension implements Extension, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(TransactionalEjbAnnotatorExtension.class.getCanonicalName());

	public <X> void processAnnotatedTypes(@Observes ProcessAnnotatedType<X> pat) {
		LOG.log(Level.FINE, "processing type {0}", pat);
		AnnotatedTypeConfigurator<X> configurator = pat.configureAnnotatedType();

		if (configurator.getAnnotated().isAnnotationPresent(Stateless.class)
			|| configurator.getAnnotated().isAnnotationPresent(Stateful.class)
			|| configurator.getAnnotated().isAnnotationPresent(Singleton.class)) {
			final Object replacementProxy = Proxy.newProxyInstance(
				getClass().getClassLoader(),
				new Class[] {
					TransactionalEjb.class },
				new AnnotationInvocationHandler(TransactionalEjb.class));

			configurator.add((Annotation) replacementProxy);
			LOG.log(
				Level.INFO,
				"Added TransactionalEjb annotation to {1}",
				new Object[] {
					replacementProxy.getClass().getName(),
					pat });
		}
	}

	static class AnnotationInvocationHandler implements InvocationHandler {

		private final Class<?> replacementAnnotation;

		AnnotationInvocationHandler(Class<?> replacementAnnotation) {
			this.replacementAnnotation = replacementAnnotation;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			switch (method.getName()) {
			case "annotationType":
				return replacementAnnotation;
			case "hashCode":
				return 0;
			case "equals":
				Object other = args[0];
				if (this == other) {
					return true;
				} else if (!replacementAnnotation.isInstance(other)) {
					return false;
				} else if (Proxy.isProxyClass(other.getClass())) {
					InvocationHandler invocationHandler = Proxy.getInvocationHandler(other);
					if (invocationHandler instanceof AnnotationInvocationHandler) {
						return invocationHandler.equals(this);
					}
				}
				return false;
			case "toString":
				return replacementAnnotation.getCanonicalName();
			default:
				return null;
			}
		}
	}
}
