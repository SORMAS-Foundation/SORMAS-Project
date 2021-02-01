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

import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.rest.swagger.AttributeConverter;
import de.symeda.sormas.rest.swagger.SormasSwaggerExtensions;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.apache.commons.collections4.SetUtils;
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

		Info info = new Info().title("SORMAS external symptom journal API")
				.version(InfoProvider.get().getVersion())
				.description(
						"The purpose of this API is to enable communication between SORMAS and other symptom journals. "
								+ "Only users with the role ``REST_EXTERNAL_VISITS_USER`` are authorized to use the endpoints. "
								+ "If you would like to receive access, please contact the System Administrator. "
								+ "For technical details please contact the dev team on gitter. "
								+ "Authentication is done using basic auth, with the user and password.")
				.contact(new Contact().url("https://gitter.im/SORMAS-Project/dev-support").name("Dev support"))
				.license(new License().name("GNU General Public License").url("https://www.gnu.org/licenses/"));

		OpenAPI openAPI = new OpenAPI().info(info);
		openAPI.addExtension("sormas-extension", new SormasSwaggerExtensions());
		ModelConverters.getInstance().addConverter(new AttributeConverter(Json.mapper()));
		SwaggerConfiguration openAPIConfiguration = new SwaggerConfiguration()
				.prettyPrint(true)
				.openAPI(openAPI)
				.readAllResources(false)
				.resourceClasses(SetUtils.hashSet(ExternalVisitsResource.class.getSimpleName()));
		OpenApiResource openApiResource = new OpenApiResource();
		openApiResource.setOpenApiConfiguration(openAPIConfiguration);
		register(openApiResource);
	}
}
