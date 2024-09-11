package de.symeda.sormas.rest.resources;

import java.util.function.UnaryOperator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.news.NewsCriteria;
import de.symeda.sormas.api.news.NewsDto;
import de.symeda.sormas.api.news.NewsIndexDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/news")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class NewsResource extends EntityDtoResource<NewsDto> {

	@POST
	@Path("/indexList")
	public Page<NewsIndexDto> getNewsIndexList(
		@RequestBody CriteriaWithSorting<NewsCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getNewsFacade().getNewsPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@Override
	public UnaryOperator<NewsDto> getSave() {
		throw new UnsupportedOperationException("Save not supported for News");
	}
}
