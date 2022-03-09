package de.symeda.sormas.rest;

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
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/travelentries")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class TravelEntryResource extends EntityDtoResource {

	@POST
	@Path("/indexList")
	public Page<TravelEntryIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<TravelEntryCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getTravelEntryFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/{uuid}")
	public TravelEntryDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getTravelEntryFacade().getByUuid(uuid);
	}

	@POST
	@Path("/push")
	public List<PushResult> postTravelEntries(@Valid List<TravelEntryDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getTravelEntryFacade()::save);
		return result;
	}

	@DELETE
	@Path("/{uuid}")
	public Response delete(@PathParam("uuid") String uuid) {
		FacadeProvider.getTravelEntryFacade().delete(uuid);
		return Response.ok("OK").build();
	}

}
