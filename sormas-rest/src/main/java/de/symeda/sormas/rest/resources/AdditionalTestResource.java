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
import de.symeda.sormas.api.sample.AdditionalTestCriteria;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/additionaltests")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Additional Tests Resource", description = "Information about additional conductable medical tests.")
public class AdditionalTestResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all additional tests from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of additional Tests for the given interval.", useReturnTypeSchema = true)
	public List<AdditionalTestDto> getAllAdditionalTests(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getAdditionalTestFacade().getAllActiveAdditionalTestsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of addtional tests that fulfill certain criteria.",
		description = "**-** tests are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200", description = "Returns a list of additional tests for the given interval.", useReturnTypeSchema = true)
	public List<AdditionalTestDto> getAllAdditionalTests(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getAdditionalTestFacade().getAllActiveAdditionalTestsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get data of additional tests based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of additional tests by their UUIDs.", useReturnTypeSchema = true)
	public List<AdditionalTestDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query additional test data entries.") List<String> uuids) {
		List<AdditionalTestDto> result = FacadeProvider.getAdditionalTestFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of additional test entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containig the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postAdditionalTests(
		@RequestBody(
			description = "List of AdditionalTestDtos to be added to the existing additional test data entries.") @Valid List<AdditionalTestDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getAdditionalTestFacade()::saveAdditionalTest);
		return result;
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available additional test data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getAdditionalTestFacade().getAllActiveUuids();
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of AdditionalTests based on AdditionalTestCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a page of additional tests that have met the filter criteria.",
		useReturnTypeSchema = true)
	public Page<AdditionalTestDto> getIndexList(
		@RequestBody(description = "Additional test based query-filter with sorting property.",
			required = true) CriteriaWithSorting<AdditionalTestCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(required = true, description = "page offset") int offset,
		@QueryParam("size") @Parameter(required = true, description = "page size") int size) {
		return FacadeProvider.getAdditionalTestFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

}
