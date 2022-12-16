package de.symeda.sormas.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/labmessages")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Lab Message", description = "Management of lab messages.")
public class LabMessageResource extends EntityDtoResource {

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a specific lab message based on its unique ID (UUID).")
	@ApiResponse(responseCode = "200", description = "Returns a lab message by its UUID.", useReturnTypeSchema = true)
	public ExternalMessageDto getByUuid(
		@Parameter(required = true, description = "Universally unique identifier to query the sample.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getExternalMessageFacade().getByUuid(uuid);
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of ExternalMessageIndexDtos based on ExternalMessageCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of lab messages that have met the filter criteria.", useReturnTypeSchema = true)
	public Page<ExternalMessageIndexDto> getIndexList(
		@RequestBody(description = "Lab message-based query-filter with sorting property.",
			required = true) CriteriaWithSorting<ExternalMessageCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getExternalMessageFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

}
