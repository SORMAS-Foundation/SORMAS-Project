package de.symeda.sormas.rest.resources;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/travelentries")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Travel Entry Resource",
	description = "Management of travel entries into a country that may need to be tracked, depending on said contry's pandemic regulations. "
		+ "Similarly to **Contacts**, travel entries can be escalated to **Cases** if the affected person is tested positive for a disease.\n\n"
		+ "See also: **Cases**, **Contacts**.")
public class TravelEntryResource extends EntityDtoResource {

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of TravelEntryIndexDto based on TravelEntryCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of tasks that have met the filter criteria.", useReturnTypeSchema = true)
	public Page<TravelEntryIndexDto> getIndexList(
		@RequestBody(description = "Query-filter based on travel entries with sorting property.",
			required = true) CriteriaWithSorting<TravelEntryCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getTravelEntryFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a specific travel entry based on its unique ID (UUID).")
	@ApiResponse(responseCode = "200", description = "Returns a travel entry by its UUID.", useReturnTypeSchema = true)
	public TravelEntryDto getByUuid(
		@Parameter(required = true, description = "Universally unique identifier to query the travel entry.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getTravelEntryFacade().getByUuid(uuid);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of travel entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postTravelEntries(
		@RequestBody(description = "List of TravelEntryDtos to be added to the existing travel entries.",
			required = true) @Valid List<TravelEntryDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getTravelEntryFacade()::save);
		return result;
	}

	@DELETE
	@Path("/{uuid}")
	@Operation(summary = "Delete a travel entry based on its unique ID (UUID).")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public Response delete(@PathParam("uuid") String uuid) {
		FacadeProvider.getTravelEntryFacade().delete(uuid, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"));
		return Response.ok("OK").build();
	}

}
