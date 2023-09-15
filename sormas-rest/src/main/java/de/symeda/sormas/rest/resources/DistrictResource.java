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
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/districts")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class DistrictResource extends EntityDtoResource<DistrictDto> {

	@GET
	@Path("/all/{since}")
	public List<DistrictDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getDistrictFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<DistrictDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getDistrictFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getDistrictFacade().getAllUuids();
	}

	@POST
	@Path("/indexList")
	public Page<DistrictIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<DistrictCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getDistrictFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/archive")
	public List<String> archive(@RequestBody List<String> uuids) {
		return FacadeProvider.getDistrictFacade().archive(uuids).stream().map(ProcessedEntity::getEntityUuid).collect(Collectors.toList());
	}

	@POST
	@Path("/dearchive")
	public List<String> dearchive(@RequestBody List<String> uuids) {
		return FacadeProvider.getDistrictFacade().dearchive(uuids).stream().map(ProcessedEntity::getEntityUuid).collect(Collectors.toList());
	}

	@Override
	public UnaryOperator<DistrictDto> getSave() {
		return FacadeProvider.getDistrictFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<DistrictDto> districtDtos) {
		return super.postEntityDtos(districtDtos);
	}
}
