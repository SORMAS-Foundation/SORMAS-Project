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
package de.symeda.sormas.rest;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.ApplicationPath;

import org.apache.commons.collections4.SetUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.AuthProvider;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.rest.resources.ExternalVisitsResource;
import de.symeda.sormas.rest.security.KeycloakFilter;
import de.symeda.sormas.rest.swagger.AttributeConverter;
import de.symeda.sormas.rest.swagger.SormasSwaggerExtensions;
import de.symeda.sormas.rest.swagger.SwaggerConfig;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * @see <a href="https://jersey.github.io/documentation/latest/index.html">Jersey documentation</a>
 */
@ApplicationPath("/")
public class RestConfig extends ResourceConfig {

	public RestConfig() {

		super(RestConfig.class);

		// Resources.
		packages(getClass().getPackage().getName());

		register(JacksonFeature.class);

		SwaggerConfig.init();

		Info info = new Info().title("SORMAS external symptom journal API")
			.version(InfoProvider.get().getVersion())
			.description(
				"The purpose of this API is to enable communication between SORMAS and other symptom journals. "
					+ "Only users with the role ``REST_EXTERNAL_VISITS_USER`` are authorized to use the endpoints. "
					+ "If you would like to receive access, please contact the System Administrator. "
					+ "For technical details please contact the dev team through GitHub Discussions. "
					+ "Authentication is done using basic auth, with the user and password.")
			.contact(
				new Contact().url("https://github.com/sormas-foundation/SORMAS-Project/discussions/categories/development-support")
					.name("Development support"))
			.license(new License().name("GNU General Public License").url("https://www.gnu.org/licenses/"));

		OpenAPI openAPI = new OpenAPI().info(info);
		openAPI.addExtension("sormas-extension", new SormasSwaggerExtensions());
		ModelConverters.getInstance().addConverter(new AttributeConverter(Json.mapper()));
		SwaggerConfiguration openAPIConfiguration = new SwaggerConfiguration().prettyPrint(true)
			.openAPI(openAPI)
			.readAllResources(false)
			.resourceClasses(SetUtils.hashSet(ExternalVisitsResource.class.getSimpleName()));
		OpenApiResource openApiResource = new OpenApiResource();
		openApiResource.setOpenApiConfiguration(openAPIConfiguration);
		register(openApiResource);
	}

	@WebListener
	public static class FilterStartupListener implements ServletContextListener {

		private final Logger logger = LoggerFactory.getLogger(getClass());

		@Override
		public void contextInitialized(ServletContextEvent sce) {
			ServletContext ctx = sce.getServletContext();
			String authenticationProvider = FacadeProvider.getConfigFacade().getAuthenticationProvider();
			if (authenticationProvider.equalsIgnoreCase(AuthProvider.KEYCLOAK)) {
				FilterRegistration.Dynamic filterRegistration = ctx.addFilter("KeycloakFilter", KeycloakFilter.class);
				filterRegistration.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
				filterRegistration.setAsyncSupported(true);
				logger.debug("Keycloak filter enabled");
			} else {
				logger.debug("Keycloak filter disabled");
			}
		}
	}

}
