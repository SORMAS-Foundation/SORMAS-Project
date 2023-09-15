/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/pointsofentry")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class PointOfEntryResource extends EntityDtoResource<PointOfEntryDto> {

	@GET
	@Path("/all/{since}")
	public List<PointOfEntryDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getPointOfEntryFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<PointOfEntryDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getPointOfEntryFacade().getByUuids(uuids);
	}

	@POST
	@Path("/indexList")
	public Page<PointOfEntryDto> getIndexList(
		@RequestBody CriteriaWithSorting<PointOfEntryCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getPointOfEntryFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getPointOfEntryFacade().getAllUuids();
	}

	@POST
	@Path("/archive")
	public List<String> archive(@RequestBody List<String> uuids) {
		return FacadeProvider.getPointOfEntryFacade().archive(uuids).stream().map(ProcessedEntity::getEntityUuid).collect(Collectors.toList());
	}

	@POST
	@Path("/dearchive")
	public List<String> dearchive(@RequestBody List<String> uuids) {
		return FacadeProvider.getPointOfEntryFacade().dearchive(uuids).stream().map(ProcessedEntity::getEntityUuid).collect(Collectors.toList());
	}

	@Override
	public UnaryOperator<PointOfEntryDto> getSave() {
		return FacadeProvider.getPointOfEntryFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<PointOfEntryDto> pointOfEntryDtos) {
		return super.postEntityDtos(pointOfEntryDtos);
	}
}
