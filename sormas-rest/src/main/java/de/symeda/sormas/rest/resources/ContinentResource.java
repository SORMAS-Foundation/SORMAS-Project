package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.continent.ContinentCriteria;
import de.symeda.sormas.api.infrastructure.continent.ContinentDto;
import de.symeda.sormas.api.infrastructure.continent.ContinentIndexDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/continents")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Continent Resource",
	description = "Access to general geographic location following the hierarchy:\n\n"
		+ "**Continent** > Subcontinent > Country > Area > Region > District > Community > Facility\n\n"
		+ "Allows countries/districts/communities to set-up their own sub-divided infrastructure conforming to the centralized SORMAS base structure.")
public class ContinentResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all avaliable continents from a date in the past until now.")
	@ApiResponse(description = "Returns a list of continents for the given time interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<ContinentDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getContinentFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get a list of continents based on their unique IDs (UUIDs).")
	@ApiResponse(description = "Returns a list of continents by UUIDs. If a UUID does not match to any continent, it is ignored.")
	public List<ContinentDto> getByUuids(
		@RequestBody(description = "List of continent UUIDs. These UUIDs are used to query continents.", required = true) List<String> uuids) {
		return FacadeProvider.getContinentFacade().getByUuids(uuids);
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of ContinentIndices based on ContinentCriteria filter params.")
	@ApiResponse(description = "Returns a page of continents that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public Page<ContinentIndexDto> getIndexList(
		@RequestBody(description = "Continent-based query-filter criteria with sorting property.",
			required = true) CriteriaWithSorting<ContinentCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(required = true, description = "page offset") int offset,
		@QueryParam("size") @Parameter(required = true, description = "page size") int size) {
		return FacadeProvider.getContinentFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available continents.")
	@ApiResponse(description = "Returns a list of available continents' UUIDs.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getContinentFacade().getAllUuids();
	}

	@POST
	@Path("/push")
	@Operation(summary = "Add a list of continents that should be created or updated.")
	@ApiResponse(description = "Returns a list with a push result for each continent.", responseCode = "200", useReturnTypeSchema = true)
	public List<PushResult> postContinents(
		@RequestBody(description = "List of continents to create or update.", required = true) @Valid List<ContinentDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getContinentFacade()::save);
		return result;
	}

	@POST
	@Path("/archive")
	@Operation(summary = "Mark continents as archived based on their unique IDs (UUIDs); i.e. deactivate.")
	@ApiResponse(description = "Returns a list of UUIDs for which archiving was successful.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> archive(
		@RequestBody(description = "List of continent UUIDs. These UUIDs denote the continents to be archived.",
			required = true) List<String> uuids) {
		return FacadeProvider.getContinentFacade().archive(uuids);
	}

	@POST
	@Path("/dearchive")
	@Operation(summary = "Remove continents from archive based on their unique IDs (UUIDs); i.e. reactivate.")
	@ApiResponse(description = "Returns a list of UUIDs for which reactivation was successful.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> dearchive(
		@RequestBody(description = "List of continent UUIDs. These UUIDs denote the continents to be reactivated from archive.",
			required = true) List<String> uuids) {
		return FacadeProvider.getContinentFacade().dearchive(uuids);
	}
}
