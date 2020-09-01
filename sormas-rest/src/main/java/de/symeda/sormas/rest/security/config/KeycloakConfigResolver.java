/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.rest.security.config;

import de.symeda.sormas.rest.security.KeycloakFilter;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.Optional;

/**
 * Loads the Keycloak configuration using JNDI.
 *
 * @author Alex Vidrean
 * @see KeycloakFilter
 * @since 07-Aug-20
 */
@Stateless
@LocalBean
public class KeycloakConfigResolver implements org.keycloak.adapters.KeycloakConfigResolver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public KeycloakDeployment resolve(HttpFacade.Request facade) {
		Optional<String> oidcJson = ConfigProvider.getConfig().getOptionalValue("sormas.rest.security.oidc.json", String.class);

		if (!oidcJson.isPresent()) {
			logger.warn(
				"Undefined KEYCLOAK configuration for sormas.rest.security.oidc.json. Configure the property or deactivate the KEYCLOAK authentication provider before proceeding");
			return new KeycloakDeployment();
		}
		return KeycloakDeploymentBuilder.build(new ByteArrayInputStream(oidcJson.get().getBytes()));
	}

}
