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
package de.symeda.sormas.ui.security.config;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.Producer;

import fish.payara.security.annotations.OpenIdAuthenticationDefinition;
import fish.payara.security.openid.OpenIdAuthenticationMechanism;
import fish.payara.security.openid.OpenIdExtension;
import fish.payara.security.openid.controller.ConfigurationController;
import fish.payara.security.openid.domain.OpenIdConfiguration;

/**
 * Since we are supporting multiple authentication mechanisms configured programmatically, we cannot use the
 * {@link OpenIdAuthenticationDefinition} annotation directly on a class. <br/>
 * <br/>
 *
 * Due to this, when trying to activate the {@link OpenIdAuthenticationMechanism} it will fail because
 * {@link OpenIdExtension} will inject a <code>null</code> {@link OpenIdAuthenticationDefinition} so the
 * producer found in {@link ConfigurationController} will fail with a NPE. <br/>
 * <br/>
 * 
 * This extension counters this behavior by overiding the producer and checking if the {@link OpenIdAuthenticationDefinition} is
 * <code>null</code>, then it will use the default definition ({@link DefaultOpenIdAuthenticationDefinition}).
 * 
 */
public class OpenIdAuthenticationExtension implements Extension {

	void onProcessProducer(@Observes @Any ProcessProducer<ConfigurationController, OpenIdConfiguration> openIdConfigurationProducer) {
		openIdConfigurationProducer.setProducer(new OpenIdConfigurationProducer(openIdConfigurationProducer.getProducer()));
	}

	private static class OpenIdConfigurationProducer implements Producer<OpenIdConfiguration> {

		private final Producer<OpenIdConfiguration> wrapped;

		public OpenIdConfigurationProducer(Producer<OpenIdConfiguration> wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public OpenIdConfiguration produce(CreationalContext<OpenIdConfiguration> ctx) {
			OpenIdAuthenticationDefinition definition = CDI.current().select(OpenIdAuthenticationDefinition.class).get();

			if (definition == null) {
				ConfigurationController configurationController = CDI.current().select(ConfigurationController.class).get();
				return configurationController.produceConfiguration(new DefaultOpenIdAuthenticationDefinition());
			}
			return wrapped.produce(ctx);
		}

		@Override
		public void dispose(OpenIdConfiguration instance) {
			wrapped.getInjectionPoints();
		}

		@Override
		public Set<InjectionPoint> getInjectionPoints() {
			return wrapped.getInjectionPoints();
		}
	}

}
