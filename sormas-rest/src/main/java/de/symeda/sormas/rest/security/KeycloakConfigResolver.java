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

package de.symeda.sormas.rest.security;

import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.spi.HttpFacade;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.io.ByteArrayInputStream;

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

	@Resource(lookup = "keycloak/json")
	private String keycloakConfig;

	@Override
	public KeycloakDeployment resolve(HttpFacade.Request facade) {
		return KeycloakDeploymentBuilder.build(new ByteArrayInputStream(keycloakConfig.getBytes()));
	}

}
