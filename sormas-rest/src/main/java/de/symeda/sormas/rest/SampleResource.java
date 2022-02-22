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

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
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
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleIndexDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/samples")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class SampleResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<SampleDto> getAllSamples(@PathParam("since") long since) {
		return FacadeProvider.getSampleFacade().getAllActiveSamplesAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<SampleDto> getAllSamples(@PathParam("since") long since, @PathParam("size") int size, @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getSampleFacade().getAllActiveSamplesAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@GET
	@Path("/{uuid}")
	public SampleDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getSampleFacade().getSampleByUuid(uuid);
	}

	@POST
	@Path("/query")
	public List<SampleDto> getByUuids(List<String> uuids) {
		List<SampleDto> result = FacadeProvider.getSampleFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/query/cases")
	public List<SampleDto> getByCaseUuids(List<String> uuids) {
		List<SampleDto> result = FacadeProvider.getSampleFacade().getByCaseUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	public List<PushResult> postSamples(@Valid List<SampleDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getSampleFacade()::saveSample);
		return result;
	}

	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getSampleFacade().getAllActiveUuids();
	}

	@GET
	@Path("/deleted/{since}")
	public List<String> getDeletedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getSampleFacade().getDeletedUuidsSince(new Date(since));
	}

	@POST
	@Path("/indexList")
	public Page<SampleIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<SampleCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getSampleFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/delete")
	public List<String> delete(List<String> uuids) {
		return FacadeProvider.getSampleFacade().deleteSamples(uuids);
	}

}
