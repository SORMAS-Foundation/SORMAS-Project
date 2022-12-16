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
import de.symeda.sormas.api.sample.PathogenTestCriteria;
import de.symeda.sormas.api.sample.PathogenTestDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/pathogentests")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Pathogen Test Resource",
	description = "Management of pathogen test data.\n\n"
		+ "Multiple pathogen tests can be performed a single **Sample** object, usually taken for a **Case** or **Contact**, "
		+ "to confirm or refute the presence of a disease.")
public class PathogenTestResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all pathogen tests from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of pathogen tests for the given time interval.", useReturnTypeSchema = true)
	public List<PathogenTestDto> getAllPathogenTests(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getPathogenTestFacade().getAllActivePathogenTestsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of pathogen tests that fulfill certain criteria.",
		description = "**-** pathogen tests are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of pathogen tests for the given interval that met the criteria.",
		useReturnTypeSchema = true)
	public List<PathogenTestDto> getAllPathogenTests(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getPathogenTestFacade().getAllActivePathogenTestsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get pathogen tests based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of pathogen tests by their UUIDs.", useReturnTypeSchema = true)
	public List<PathogenTestDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query pathogentest entries.", required = true) List<String> uuids) {
		List<PathogenTestDto> result = FacadeProvider.getPathogenTestFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/query/samples")
	@Operation(summary = "Get all pathogen tests for specific samples represented by their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of pathogen tests.", useReturnTypeSchema = true)
	public List<PathogenTestDto> getBySampleUuids(
		@RequestBody(description = "List of sample UUIDs used to query pathogen test entries.", required = true) List<String> sampleUuids) {
		List<PathogenTestDto> result = FacadeProvider.getPathogenTestFacade().getBySampleUuids(sampleUuids);
		return result;
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of pathogen tests to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded pathogen test.",
		useReturnTypeSchema = true)
	public List<PushResult> postPathogenTests(
		@RequestBody(description = "List of PathogenTestDtos to be added to the server.", required = true) @Valid List<PathogenTestDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getPathogenTestFacade()::savePathogenTest);
		return result;
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available pathogen tests.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getPathogenTestFacade().getAllActiveUuids();
	}

	@GET
	@Path("/deleted/{since}")
	@Operation(summary = "Get the unique IDs of all pathogen test data that has been deleted during the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getDeletedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getPathogenTestFacade().getDeletedUuidsSince(new Date(since));
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of PathogenTestDtos based on PathogenTestCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a page of pathogen tests that have met the filter criteria.",
		useReturnTypeSchema = true)
	public Page<PathogenTestDto> getIndexList(
		@RequestBody(description = "PathogenTest-based query-filter with sorting property.",
			required = true) CriteriaWithSorting<PathogenTestCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getPathogenTestFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}
}
