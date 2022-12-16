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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.report.WeeklyReportDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/weeklyreports")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Weekly Report Resource", description = "Management of weekly reports generated for an epi week.")
public class WeeklyReportResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all weekly reports from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of weekly reports for the given interval.", useReturnTypeSchema = true)
	public List<WeeklyReportDto> getAllWeeklyReports(
		@Context SecurityContext sc,
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getWeeklyReportFacade().getAllWeeklyReportsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of weekly reports that fulfill certain criteria.",
		description = "**-** weekly reports are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** number of results does not exceed a given number [*size*]\n\n" + "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]")
	@ApiResponse(responseCode = "200", description = "Returns a list of weekly reports for the given interval.", useReturnTypeSchema = true)
	public List<WeeklyReportDto> getAllWeeklyReports(
		@Context SecurityContext sc,
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getWeeklyReportFacade().getAllWeeklyReportsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get weekly report data based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of weekly reports by their UUIDs.", useReturnTypeSchema = true)
	public List<WeeklyReportDto> getByUuids(
		@Context SecurityContext sc,
		@RequestBody(description = "List of UUIDs used to query weekly report entries.", required = true) List<String> uuids) {
		List<WeeklyReportDto> result = FacadeProvider.getWeeklyReportFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of weekly reports to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded weekly report.",
		useReturnTypeSchema = true)
	public List<PushResult> postWeeklyReports(
		@RequestBody(description = "List of WeeklyReportDtos to be added to the server.", required = true) @Valid List<WeeklyReportDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getWeeklyReportFacade()::saveWeeklyReport);
		return result;
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available weekly reports.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids(@Context SecurityContext sc) {
		return FacadeProvider.getWeeklyReportFacade().getAllUuids();
	}
}
