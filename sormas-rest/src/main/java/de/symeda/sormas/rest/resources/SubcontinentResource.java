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
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentCriteria;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentDto;
import de.symeda.sormas.api.infrastructure.subcontinent.SubcontinentIndexDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/subcontinents")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Subcontinent Resource",
	description = "Access to general geographic location following the hierarchy:\n\n"
		+ "Continent > **Subcontinent** > Country > Area > Region > District > Community > Facility\n\n"
		+ "Allows countries/districts/communities to set-up their own sub-divided infrastructure conforming to the centralized SORMAS base structure.")
public class SubcontinentResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all avaliable sub-continents from a date in the past until now.")
	@ApiResponse(description = "Returns a list of sub-continents for the given time interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<SubcontinentDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT.") @PathParam("since") long since) {
		return FacadeProvider.getSubcontinentFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get a list of sub-continents based on their unique IDs (UUIDs).")
	@ApiResponse(description = "Returns a list of sub-continents by UUIDs. If a UUID does not match to any continent, it is ignored.")
	public List<SubcontinentDto> getByUuids(
		@RequestBody(description = "List of sub-continent UUIDs. These UUIDs are used to query sub-continents.",
			required = true) List<String> uuids) {
		return FacadeProvider.getSubcontinentFacade().getByUuids(uuids);
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of SubcontinentIndices based on SubcontinentCriteria filter params.")
	@ApiResponse(description = "Returns a page of sub-continents that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public Page<SubcontinentIndexDto> getIndexList(
		@RequestBody(description = "Continent-based query-filter criteria with sorting property.",
			required = true) CriteriaWithSorting<SubcontinentCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(required = true, description = "page offset") int offset,
		@QueryParam("size") @Parameter(required = true, description = "page size") int size) {
		return FacadeProvider.getSubcontinentFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available sub-continents.")
	@ApiResponse(description = "Returns a list of available sub-continents' UUIDs.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getSubcontinentFacade().getAllUuids();
	}

	@POST
	@Path("/push")
	@Operation(summary = "Add a list of sub-continents that should be created or updated.")
	@ApiResponse(description = "Returns a list with a push result for each sub-continent.", responseCode = "200", useReturnTypeSchema = true)
	public List<PushResult> postSubcontinents(
		@RequestBody(description = "List of sub-continents to create or update.", required = true) @Valid List<SubcontinentDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getSubcontinentFacade()::save);
		return result;
	}

	@POST
	@Path("/archive")
	@Operation(summary = "Mark sub-continents as archived based on their unique IDs (UUIDs); i.e. deactivate.")
	@ApiResponse(description = "Returns a list of UUIDs for which archiving was successful.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> archive(
		@RequestBody(description = "List of sub-continent UUIDs. These UUIDs denote the sub-continents to be archived.",
			required = true) List<String> uuids) {
		return FacadeProvider.getSubcontinentFacade().archive(uuids);
	}

	@POST
	@Path("/dearchive")
	@Operation(summary = "Remove sub-continents from archive based on their unique IDs (UUIDs); i.e. reactivate.")
	@ApiResponse(description = "Returns a list of UUIDs for which reactivation was successful.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> dearchive(
		@RequestBody(description = "List of sub-continent UUIDs. These UUIDs denote the sub-continents to be reactivated from archive.",
			required = true) List<String> uuids) {
		return FacadeProvider.getSubcontinentFacade().dearchive(uuids);
	}
}
