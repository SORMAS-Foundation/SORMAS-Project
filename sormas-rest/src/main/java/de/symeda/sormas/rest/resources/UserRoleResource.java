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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserRoleDto;

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
@Path("/userroles")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "User Role Resource",
	description = "Management of SORMAS user roles granting (read-only) access to available roles and their corresponding rights on the server.\n\n"
		+ "Also see **User Resource** and the SORMAS documentation: https://www.sormas-oegd.de/download/10068/")
public class UserRoleResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all available user roles recognized by the server from a date in the past until now.")
	@ApiResponse(description = "Returns a list of user roles for the given time interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<UserRoleDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getUserRoleFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available user roles.")
	@ApiResponse(description = "Returns a list of available user roles' UUIDs.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getUserRoleFacade().getAllUuids();
	}

	@GET
	@Path("/deleted/{since}")
	@Operation(summary = "Get all user roles that have been removed from the server since a given date in the past until now.")
	@ApiResponse(description = "Returns a list of removed user roles for the given time interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> getDeletedUuids(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getUserRoleFacade().getDeletedUuids(new Date(since));
	}
}
