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

import java.util.List;
import java.util.function.UnaryOperator;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.travelentry.TravelEntryCriteria;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.travelentry.TravelEntryIndexDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/travelentries")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class TravelEntryResource extends EntityDtoResource<TravelEntryDto> {

	@POST
	@Path("/indexList")
	public Page<TravelEntryIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<TravelEntryCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getTravelEntryFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/{uuid}")
	public TravelEntryDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getTravelEntryFacade().getTravelEntryByUuid(uuid);
	}

	@DELETE
	@Path("/{uuid}")
	public Response delete(@PathParam("uuid") String uuid) {
		FacadeProvider.getTravelEntryFacade().delete(uuid, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"));
		return Response.ok("OK").build();
	}

	@Override
	public UnaryOperator<TravelEntryDto> getSave() {
		return FacadeProvider.getTravelEntryFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<TravelEntryDto> travelEntryDtos) {
		return super.postEntityDtos(travelEntryDtos);
	}
}
