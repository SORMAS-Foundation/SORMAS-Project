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
package de.symeda.sormas.rest.resources;

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
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey
 *      documentation</a>
 * @see <a href=
 *      "https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey
 *      documentation HTTP Methods</a>
 *
 */
@Path("/outbreaks")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Outbreak Resource",
	description = "Management of outbreak data. Outbreaks are not connected to cases by default but rather serve as early indicators for a disease to occur in a certain district.")
public class OutbreakResource extends EntityDtoResource {

	@GET
	@Path("/active/{since}")
	@Operation(summary = "Get all active outbreaks from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of active outbreaks for the given interval.", useReturnTypeSchema = true)
	public List<OutbreakDto> getActiveSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getOutbreakFacade().getActiveAfter(new Date(since));
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available outbreaks.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getActiveUuids() {
		return FacadeProvider.getOutbreakFacade().getActiveUuidsAfter(null);
	}

	@GET
	@Path("/inactive/{since}")
	@Operation(summary = "Get the unique IDs of all outbreak data that has been inactive during the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getInactiveUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getOutbreakFacade().getInactiveUuidsAfter(new Date(since));
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a list of OutbreakDtos based on OutbreakCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of outbreaks that have met the filter criteria.", useReturnTypeSchema = true)
	public Page<OutbreakDto> getIndexList(
		@RequestBody(description = "Outbreak-based query-filter with sorting property.",
			required = true) CriteriaWithSorting<OutbreakCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getOutbreakFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of outbreaks to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded outbreak.",
		useReturnTypeSchema = true)
	public List<PushResult> postOutbreak(
		@RequestBody(description = "List of OutbreaksDtos to be added to the server.", required = true) @Valid List<OutbreakDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getOutbreakFacade()::saveOutbreak);
	}

}
