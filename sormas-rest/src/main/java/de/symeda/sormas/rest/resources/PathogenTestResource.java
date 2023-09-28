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

import javax.ws.rs.Consumes;
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
import de.symeda.sormas.api.sample.PathogenTestCriteria;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/pathogentests")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class PathogenTestResource extends EntityDtoResource<PathogenTestDto> {

	@GET
	@Path("/all/{since}")
	public List<PathogenTestDto> getAllPathogenTests(@PathParam("since") long since) {
		return FacadeProvider.getPathogenTestFacade().getAllActivePathogenTestsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<PathogenTestDto> getAllPathogenTests(
		@PathParam("since") long since,
		@PathParam("size") int size,
		@PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getPathogenTestFacade().getAllActivePathogenTestsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	public List<PathogenTestDto> getByUuids(List<String> uuids) {
		final List<PathogenTestDto> byUuids = FacadeProvider.getPathogenTestFacade().getByUuids(uuids);
		return byUuids;
	}

	@POST
	@Path("/query/samples")
	public List<PathogenTestDto> getBySampleUuids(List<String> sampleUuids) {
		return FacadeProvider.getPathogenTestFacade().getBySampleUuids(sampleUuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids() {
		final List<String> allActiveUuids = FacadeProvider.getPathogenTestFacade().getAllActiveUuids();
		return allActiveUuids;
	}

	@GET
	@Path("/deleted/{since}")
	public List<String> getDeletedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getPathogenTestFacade().getDeletedUuidsSince(new Date(since));
	}

	@POST
	@Path("/indexList")
	public Page<PathogenTestDto> getIndexList(
		@RequestBody CriteriaWithSorting<PathogenTestCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getPathogenTestFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@Override
	public UnaryOperator<PathogenTestDto> getSave() {
		return FacadeProvider.getPathogenTestFacade()::savePathogenTest;
	}

	@Override
	public Response postEntityDtos(List<PathogenTestDto> pathogenTestDtos) {
		return super.postEntityDtos(pathogenTestDtos);
	}
}
