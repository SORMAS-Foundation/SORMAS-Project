package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/featureconfigurations")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Feature Configuration Resource",
	description = "Read-only access to available functionality of the server. The SORMAS system provides feature configutions to enable or disable functions such as Contact Tracing, Case Surveillance, etc.")
public class FeatureConfigurationResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all feature configurations from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of feature configurations for the given interval.", useReturnTypeSchema = true)
	public List<FeatureConfigurationDto> getAllFeatureConfigurations(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getFeatureConfigurationFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get feature configurations based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of feature configurations by their UUIDs.", useReturnTypeSchema = true)
	public List<FeatureConfigurationDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query feature configuration entries.", required = true) List<String> uuids) {
		return FacadeProvider.getFeatureConfigurationFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available feature configurations.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getFeatureConfigurationFacade().getAllUuids();
	}

	@GET
	@Path("/deleted/{since}")
	@Operation(summary = "Get the unique IDs of all feature configuration data that has been deleted during the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getDeletedUuids(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getFeatureConfigurationFacade().getDeletedUuids(new Date(since));
	}
}
