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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/treatments")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class TreatmentResource extends EntityDtoResource<TreatmentDto> {

	@GET
	@Path("/all/{since}")
	public List<TreatmentDto> getAllTreatments(@PathParam("since") long since) {
		return FacadeProvider.getTreatmentFacade().getAllActiveTreatmentsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<TreatmentDto> getAllTreatments(
		@PathParam("since") long since,
		@PathParam("size") int size,
		@PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getTreatmentFacade().getAllActiveTreatmentsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	public List<TreatmentDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getTreatmentFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getTreatmentFacade().getAllActiveUuids();
	}

	@POST
	@Path("/indexList")
	public List<TreatmentIndexDto> getIndexList(@RequestBody CriteriaWithSorting<TreatmentCriteria> criteriaWithSorting) {
		return FacadeProvider.getTreatmentFacade().getIndexList(criteriaWithSorting.getCriteria());
	}

	@Override
	public UnaryOperator<TreatmentDto> getSave() {
		return FacadeProvider.getTreatmentFacade()::saveTreatment;
	}

	@Override
	public Response postEntityDtos(List<TreatmentDto> treatmentDtos) {
		return super.postEntityDtos(treatmentDtos);
	}
}
