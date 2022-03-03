/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityIndexDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/facilities")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class FacilityResource extends EntityDtoResource {

	@GET
	@Path("/region/{regionUuid}/{since}")
	public List<FacilityDto> getAllByRegion(@PathParam("regionUuid") String regionUuid, @PathParam("since") long since) {
		return FacadeProvider.getFacilityFacade().getAllByRegionAfter(regionUuid, new Date(since));
	}

	@GET
	@Path("/general/{since}")
	public List<FacilityDto> getAllWithoutRegion(@PathParam("since") long since) {
		return FacadeProvider.getFacilityFacade().getAllWithoutRegionAfter(new Date(since));
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getFacilityFacade().getAllUuids();
	}

	@POST
	@Path("/query")
	public List<FacilityDto> getByUuids(List<String> uuids) {

		List<FacilityDto> result = FacadeProvider.getFacilityFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/indexList")
	public Page<FacilityIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<FacilityCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getFacilityFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/push")
	public List<PushResult> postSubcontinents(@Valid List<FacilityDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getFacilityFacade()::save);
		return result;
	}
}
