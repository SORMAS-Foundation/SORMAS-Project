/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System Copyright © 2016-2018
 * Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
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
import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
@Path("/actions")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Action Resource",
	description = "Management of event-related actions.\n\n"
		+ "Actions are linked to **Events**, consist of a description and execution comment and can be tracked via their *status*. Their functionality is similar but more limited when compared to *Tasks*.\n\n"
		+ "Also see: **EventResource**, **TaskResource**")
public class ActionResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all actions from a date in the past until now")
	@ApiResponse(description = "Returns a list of actions for the given interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<ActionDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getActionFacade().getAllActiveActionsAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get actions based on their unique IDs (UUIDs).")
	@ApiResponse(description = "Returns a list of actions by their UUIDs. If a UUID does not match to any action, it is ignored.",
		responseCode = "200",
		useReturnTypeSchema = true)
	public List<ActionDto> getByUuids(@RequestBody(description = "List of UUIDs used to query action entries.", required = true) List<String> uuids) {

		List<ActionDto> result = FacadeProvider.getActionFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of actions to the server.")
	@ApiResponse(description = "Returns a list containing the upload success status of each uploaded action.",
		responseCode = "200",
		useReturnTypeSchema = true)
	public List<PushResult> postActions(
		@RequestBody(description = "List of ActionDtos to be added to the server.", required = true) @Valid List<ActionDto> dtos) {

		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getActionFacade()::saveAction);
		return result;
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available actions.")
	@ApiResponse(description = "Returns a list of strings (UUIDs).", responseCode = "200", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getActionFacade().getAllActiveUuids();
	}

	@POST
	@Path("/indexEventActionList")
	@Operation(summary = "Get a page of EventActions based on the EventCriteria of the event they relate to.")
	@ApiResponse(description = "Returns a page of actions that have met the event-based filter criteria.",
		responseCode = "200",
		useReturnTypeSchema = true)
	public Page<EventActionIndexDto> getEventActionIndexList(
		@RequestBody(description = "Event-based query-filter with sorting properties.",
			required = true) CriteriaWithSorting<EventCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(required = true, description = "page offset") int offset,
		@QueryParam("size") @Parameter(required = true, description = "page size") int size) {
		return FacadeProvider.getActionFacade()
			.getEventActionIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/indexActionList")
	@Operation(summary = "Get a page of ActionDtos based on ActionCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of actions that have met the filter criteria.", useReturnTypeSchema = true)
	public Page<ActionDto> getActionList(
		@RequestBody(description = "Action-based query-filter with sorting properties.",
			required = true) CriteriaWithSorting<ActionCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(required = true, description = "page offset") int offset,
		@QueryParam("size") @Parameter(required = true, description = "page size") int size) {
		return FacadeProvider.getActionFacade()
			.getActionPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

}
