package de.symeda.sormas.rest;

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
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/continents")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ContinentResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<ContinentDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getContinentFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<ContinentDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getContinentFacade().getByUuids(uuids);
	}

	@POST
	@Path("/indexList")
	public Page<ContinentIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<ContinentCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getContinentFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getContinentFacade().getAllUuids();
	}

	@POST
	@Path("/push")
	public List<PushResult> postContinents(@Valid List<ContinentDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getContinentFacade()::save);
		return result;
	}
}
