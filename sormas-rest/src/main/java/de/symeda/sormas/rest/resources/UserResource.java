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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.task.TaskContextIndexCriteria;
import de.symeda.sormas.api.user.UserCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceWithTaskNumbersDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "User Resource",
	description = "SORMAS user management granting (read-only) access to registered users on the server.\n\n"
		+ "Also see **User Role Resource** and the SORMAS documentation: https://www.sormas-oegd.de/download/10068/")
public class UserResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all users registered at the server from a date in the past until now.")
	@ApiResponse(description = "Returns a list of users for the given time interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<UserDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT.") @PathParam("since") long since) {
		return FacadeProvider.getUserFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get a list of users based on their unique IDs (UUIDs).")
	@ApiResponse(description = "Returns a list of users by UUIDs. If a UUID does not match to any user, it is ignored.")
	public List<UserDto> getByUuids(
		@RequestBody(description = "List of user UUIDs. These UUIDs are used to query users.", required = true) List<String> uuids) {
		List<UserDto> result = FacadeProvider.getUserFacade().getByUuids(uuids);
		return result;
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all registered users.")
	@ApiResponse(description = "Returns a list of registered users' UUIDs.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getUserFacade().getAllUuids();
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of users based on some user-specific filter params.")
	@ApiResponse(description = "Returns a page of users that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public Page<UserDto> getIndexList(
		@RequestBody(description = "User-based query-filter criteria with sorting property.",
			required = true) CriteriaWithSorting<UserCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(required = true, description = "page offset") int offset,
		@QueryParam("size") @Parameter(required = true, description = "page size") int size) {
		return FacadeProvider.getUserFacade().getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a single user based on their unique ID (UUID).")
	@ApiResponse(description = "Returns a user. If the UUID does not match any registered user, what then?",
		responseCode = "200",
		useReturnTypeSchema = true)
	public UserDto getByUuid(
		@Parameter(required = true, description = "Universally unique identifier (UUID) to query a single user.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getUserFacade().getByUuid(uuid);
	}

	@POST
	@Path("/userReferenceWithNoOfTask")
	@Operation(summary = "Get a single user based on their currently open tasks.")
	@ApiResponse(description = "Returns a reference to the user that met the task number criteria.", responseCode = "200", useReturnTypeSchema = true)
	public List<UserReferenceWithTaskNumbersDto> getUsersWithTaskNumbers(
		@RequestBody(description = "Task-based filter criteria.", required = true) TaskContextIndexCriteria taskContextIndexCriteria) {
		return FacadeProvider.getUserFacade().getAssignableUsersWithTaskNumbers(taskContextIndexCriteria);
	}
}
