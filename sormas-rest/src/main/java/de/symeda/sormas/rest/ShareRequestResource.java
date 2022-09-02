package de.symeda.sormas.rest;

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
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/sharerequests")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ShareRequestResource extends EntityDtoResource {

	@POST
	@Path("/indexList")
	public Page<ShareRequestIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<ShareRequestCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getSormasToSormasShareRequestFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/{uuid}")
	public SormasToSormasShareRequestDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getSormasToSormasShareRequestFacade().getShareRequestByUuid(uuid);
	}

}
