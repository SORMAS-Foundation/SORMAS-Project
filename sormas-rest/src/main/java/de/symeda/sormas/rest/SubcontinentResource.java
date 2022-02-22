package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/subcontinents")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class SubcontinentResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<SubcontinentDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getSubcontinentFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<SubcontinentDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getSubcontinentFacade().getByUuids(uuids);
	}

	@POST
	@Path("/indexList")
	public Page<SubcontinentIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<SubcontinentCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getSubcontinentFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getSubcontinentFacade().getAllUuids();
	}

	@POST
	@Path("/push")
	public List<PushResult> postSubcontinents(@Valid List<SubcontinentDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getSubcontinentFacade()::save);
		return result;
	}
}
