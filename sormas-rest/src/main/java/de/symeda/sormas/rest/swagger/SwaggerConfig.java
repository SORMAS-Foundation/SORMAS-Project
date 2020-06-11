/*******************************************************************************
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.rest.swagger;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;

/**
 * SORMAS Swagger Configuration.
 * This class is the preferred spot for specifying API-related Swagger metadata via Swagger annotations,
 * as well as for Swagger extension registration.
 *
 * @author Jan-Niklas Brandes
 */
public class SwaggerConfig {

	static {
		// Real initialization routine
		registerExtensions();
	}

	public static void init() {
		// Solemnly there for triggering the static initializer
	}

	/**
	 * Register Swagger Generator Extensions
	 */
	private static void registerExtensions() {

		SormasSwaggerExtensions sormasSwaggerExtension = new SormasSwaggerExtensions();

		// Operations-Level extensions
		List<OpenAPIExtension> swaggerExtensions = new ArrayList<>(OpenAPIExtensions.getExtensions());
		swaggerExtensions.add(sormasSwaggerExtension);

		OpenAPIExtensions.setExtensions(swaggerExtensions);

		// Schema-Level extensions
		ModelConverters modelConverters = ModelConverters.getInstance();
		modelConverters.addConverter(sormasSwaggerExtension);
	}
}
