package de.symeda.sormas.rest.external;

import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.google.common.collect.Sets;

import de.symeda.sormas.rest.swagger.SwaggerConfig;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Resource configuration used only for external resources i.e. for external systems which communicate with SORMAS
 * Separate from the other resource configuration in order to limit create the swagger documentation only for resources in this package
 */
@ApplicationPath("/")
public class ExternalRestResourceConfig extends ResourceConfig {

	@Context
	private ServletConfig servletConfig;

	public ExternalRestResourceConfig() {

		super(ExternalRestResourceConfig.class);

		packages(getClass().getPackage().getName());
		register(RolesAllowedDynamicFeature.class);
		register(JacksonFeature.class);

		SwaggerConfig.init();

		Info info = new Info().title("SORMAS external symptom journal API")
			.description(
				"The purpose of this API is to enable communication between SORMAS and other symptom journals. "
					+ "Only users with the role ``REST_EXTERNAL_VISITS_USER`` are authorized to use the endpoints. "
					+ "If you would like to receive access, please contact the team to have a user set up. "
					+ "Authentication is done using basic auth, with the user and password.")
			.contact(new Contact().url("https://gitter.im/SORMAS-Project/dev-support"))
			.license(new License().name("GNU General Public License").url("https://www.gnu.org/licenses/"));

		Reader reader = new Reader(new OpenAPI().info(info));
		OpenAPI openAPI = reader.read(ExternalVisitsResource.class);
		SwaggerConfiguration openAPIConfiguration = new SwaggerConfiguration().prettyPrint(true)
			.openAPI(openAPI)
			.resourceClasses(Sets.newHashSet(ExternalVisitsResource.class.getSimpleName()));
		OpenApiResource openApiResource = new OpenApiResource();
		openApiResource.setOpenApiConfiguration(openAPIConfiguration);
		register(openApiResource);
	}
}
