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
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey
 *      documentation</a>
 * @see <a href=
 *      "https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey
 *      documentation HTTP Methods</a>
 *
 */
@Path("/persons")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Person Resource",
	description = "Management of persons that have a **Case** of or **Contact** with a supported disease. To create a completely new person use \"pushWithPerson\""
		+ " endpoint of **Case Resource** or **Contact Resource**.\n\n"
		+ "Since a person that never was associated with a disease in any way is not relevant to SORMAS, creating a stand-alone person without a corresponding case or contact is not supported.")
public class PersonResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all person data from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of all person data for the given interval.", useReturnTypeSchema = true)
	public List<PersonDto> getAllPersons(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getPersonFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of persons that fulfill certain criteria.",
		description = "**-** persons that are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200", description = "Returns a list of persons for the given interval.", useReturnTypeSchema = true)
	public List<PersonDto> getAllPersons(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getPersonFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get person data entries based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of person data entries by their UUIDs.", useReturnTypeSchema = true)
	public List<PersonDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query person data entries.", required = true) List<String> uuids) {
		return FacadeProvider.getPersonFacade().getByUuids(uuids);
	}

	@POST
	@Path("/query/byExternalIds")
	@Operation(
		summary = "Get person data entries based on their optionally assigned external IDs. Using a empty list entry to query all entries without a external ID is not supported.")
	@ApiResponse(responseCode = "200", description = "Returns a list of person data entries by their external IDs.", useReturnTypeSchema = true)
	public List<PersonDto> getByExternalIds(
		@RequestBody(description = "List of external IDs used to query person data entries.", required = true) List<String> externalIds) {
		return FacadeProvider.getPersonFacade().getByExternalIds(externalIds);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of person data entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postPersons(
		@RequestBody(description = "List of PersonDtos to update existing contact data entries.", required = true) @Valid List<PersonDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getPersonFacade()::save);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available person data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getPersonFacade().getAllUuids();
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a single contact based on its universally unique ID (UUID).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Returns a contact for the given UUID.", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "204", description = "Contact with the given UUID cannot be found.", useReturnTypeSchema = false) })
	public PersonDto getByUuid(@Parameter(required = true, description = "UUID of person.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getPersonFacade().getByUuid(uuid);
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of Persons based on PersonCriteria filter params.")
	@ApiResponse(description = "Returns a page of persons that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public Page<PersonIndexDto> getIndexList(
		@RequestBody(
			description = "Person based query-filter criteria with sorting proprerty.") CriteriaWithSorting<PersonCriteria> criteriaWithSorting,
		@Parameter(description = "page offset", required = true) @QueryParam("offset") int offset,
		@Parameter(description = "page size", required = true) @QueryParam("size") int size) {
		return FacadeProvider.getPersonFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/externalData")
	@Operation(summary = "Submit a list of ExternalData objects to update the external data entries in the corresponding persons.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public Response updateExternalData(@Valid List<ExternalDataDto> externalData) {
		try {
			FacadeProvider.getPersonFacade().updateExternalData(externalData);
			return Response.status(Response.Status.OK).build();
		} catch (ExternalDataUpdateException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path("/similarPersons")
	@Operation(summary = "Get a list of persons based on PersonSimilarityCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a list of persons that met the filter criteria", useReturnTypeSchema = true)
	public List<SimilarPersonDto> getSimilarPersons(
		@RequestBody(description = "Person similarity based query-filter criteria with sorting proprerty.") PersonSimilarityCriteria criteria) {
		return FacadeProvider.getPersonFacade().getSimilarPersonDtos(criteria);
	}
}
