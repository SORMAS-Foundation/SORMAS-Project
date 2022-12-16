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
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskIndexDto;

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
@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Task Resource",
	description = "Access to broad task functionality.\n\n"
		+ "Tasks are related to **Cases**, **Contacts** or **Travel Entries** and can be scheduled, assigned to users, and tracked via their *status*.")
public class TaskResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all active tasks from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of active tasks for the given interval.", useReturnTypeSchema = true)
	public List<TaskDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getTaskFacade().getAllActiveTasksAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of active tasks that fulfill certain criteria.",
		description = "**-** tasks are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of active tasks for the given interval that met the criteria.",
		useReturnTypeSchema = true)
	public List<TaskDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getTaskFacade().getAllActiveTasksAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get tasks based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of tasks by their UUIDs.", useReturnTypeSchema = true)
	public List<TaskDto> getByUuids(@RequestBody(description = "List of UUIDs used to query task entries.", required = true) List<String> uuids) {

		List<TaskDto> result = FacadeProvider.getTaskFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of tasks to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded task.",
		useReturnTypeSchema = true)
	public List<PushResult> postTasks(
		@RequestBody(description = "List of TaskDtos to be added to the server.", required = true) @Valid List<TaskDto> dtos) {

		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getTaskFacade()::saveTask);
		return result;
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available tasks.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getTaskFacade().getAllActiveUuids();
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of TaskIndexDtos based on TaskCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of tasks that have met the filter criteria.", useReturnTypeSchema = true)
	public Page<TaskIndexDto> getIndexList(
		@RequestBody(description = "Task-based query-filter with sorting property.",
			required = true) CriteriaWithSorting<TaskCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getTaskFacade().getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a specific task based on its unique ID (UUID).")
	@ApiResponse(responseCode = "200", description = "Returns a task by its UUID.", useReturnTypeSchema = true)
	public TaskDto getByUuid(
		@Parameter(required = true, description = "Universally unique identifier to query the task.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getTaskFacade().getByUuid(uuid);
	}

	@GET
	@Path("/archived/{since}")
	@Operation(summary = "Get all archived tasks from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of UUIDs of archived tasks for the given interval.", useReturnTypeSchema = true)
	public List<String> getArchivedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getTaskFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/obsolete/{since}")
	@Operation(summary = "Get all obsolete tasks from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of UUIDs of obsolete tasks for the given interval.", useReturnTypeSchema = true)
	public List<String> getObsoleteUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getTaskFacade().getObsoleteUuidsSince(new Date(since));
	}

	@POST
	@Path("/delete")
	@Operation(summary = "Delete tasks based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of UUIDs of tasks for which deletion was successful.",
		useReturnTypeSchema = true)
	public List<String> delete(@RequestBody(description = "List of UUIDs denoting the tasks to be deleted.", required = true) List<String> uuids) {
		return FacadeProvider.getTaskFacade().deleteTasks(uuids);
	}

}
