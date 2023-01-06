package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.area.AreaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/areas")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Area Resource",
	description = "Access to general geographic location following the hierarchy:\n\n"
		+ "Continent > Subcontinent > Country > **Area** > Region > District > Community > Facility\n\n"
		+ "Allows countries/districts/communities to set-up their own sub-divided infrastructure conforming to the centralized SORMAS base structure.")
public class AreaResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all avaliable areas from a date in the past until now.")
	@ApiResponse(description = "Returns a list of areas for the given time interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<AreaDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT.") @PathParam("since") long since) {
		return FacadeProvider.getAreaFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get a list of areas based on their unique IDs (UUIDs).")
	@ApiResponse(description = "Returns a list of areas by UUIDs. If a UUID does not match to any area, it is ignored.")
	public List<AreaDto> getByUuids(
		@RequestBody(description = "List of area UUIDs. These UUIDs are used to query areas.", required = true) List<String> uuids) {
		List<AreaDto> result = FacadeProvider.getAreaFacade().getByUuids(uuids);
		return result;
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available areas.")
	@ApiResponse(description = "Returns a list of available area UUIDs", responseCode = "200", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getAreaFacade().getAllUuids();
	}
}
