package de.symeda.sormas.rest.resources;

import javax.ws.rs.Consumes;
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
import de.symeda.sormas.api.sormastosormas.share.ShareRequestCriteria;
import de.symeda.sormas.api.sormastosormas.share.ShareRequestIndexDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/sharerequests")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Share Request Resource",
	description = "Management of of sormas-to-sormas share-requests. See also **Sormas to Sormas Resource**, **Case**, **Contact**, **Event**, **Event participant** and **Person**.")
public class ShareRequestResource extends EntityDtoResource {

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of ShareRequests based on ShareRequestCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of share-requests that met the filter criteria.", useReturnTypeSchema = true)
	public Page<ShareRequestIndexDto> getIndexList(
		@RequestBody(description = "Share-request based query-filter criteria with sorting proprerty.",
			required = true) CriteriaWithSorting<ShareRequestCriteria> criteriaWithSorting,
		@Parameter(description = "page offset", required = true) @QueryParam("offset") int offset,
		@Parameter(description = "page size", required = true) @QueryParam("size") int size) {
		return FacadeProvider.getSormasToSormasShareRequestFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a single share-request based on its universally unique ID (UUID).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Returns a share-request for the given UUID.", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "204", description = "Share-request with the given UUID cannot be found.", useReturnTypeSchema = false) })
	public SormasToSormasShareRequestDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getSormasToSormasShareRequestFacade().getShareRequestByUuid(uuid);
	}

}
