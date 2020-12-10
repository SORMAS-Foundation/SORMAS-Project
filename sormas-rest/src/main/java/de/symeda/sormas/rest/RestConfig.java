/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import de.symeda.sormas.rest.swagger.SwaggerConfig;

/**
 * @see <a href="https://jersey.github.io/documentation/latest/index.html">Jersey documentation</a>
 */
@ApplicationPath("/")
public class RestConfig extends ResourceConfig {

	public RestConfig() {

		super(RestConfig.class);

		// Resources.
		packages(getClass().getPackage().getName());

		// as described in https://jersey.github.io/documentation/latest/security.html
		register(RolesAllowedDynamicFeature.class);

		register(JacksonFeature.class);

		SwaggerConfig.init();
	}
}
