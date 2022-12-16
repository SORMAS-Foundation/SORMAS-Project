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
import de.symeda.sormas.api.caze.CoreAndPersonDto;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
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
@Path("/contacts")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Contact Resource",
	description = "Management of contact tracking data. Contacts are usually created for persons that had an encounter with a known case and bear the risk of being infected themselves.\n\n"
		+ "A contact entry can be escalated to a case.\n\n" + "See also: **Cases**, **Travel Entry**.")
public class ContactResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all contact data from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of all contact data for the given interval.", useReturnTypeSchema = true)
	public List<ContactDto> getAllContacts(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getContactFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of contacts that fulfill certain criteria.",
		description = "**-** contacts that are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200", description = "Returns a list of contacts for the given interval.", useReturnTypeSchema = true)
	public List<ContactDto> getAllContacts(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getContactFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get contact data based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list contact data by their UUIDs.", useReturnTypeSchema = true)
	public List<ContactDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query contact data entries.", required = true) List<String> uuids) {

		List<ContactDto> result = FacadeProvider.getContactFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/query/persons")
	@Operation(summary = "Get contact data based on the unique IDs of the person that had the contact.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of contact data based on the UUIDs of the queried persons.",
		useReturnTypeSchema = true)
	public List<ContactDto> getByPersonUuids(
		@RequestBody(description = "List of person UUIDs used to query contact data entries.", required = true) List<String> uuids) {
		return FacadeProvider.getContactFacade().getByPersonUuids(uuids);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of contact data entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postContacts(
		@RequestBody(description = "List of ContactDtos to be added to the existing contact data entries.",
			required = true) @Valid List<ContactDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getContactFacade()::save);
		return result;
	}

	@POST
	@Operation(summary = "Submit a single CoreAndPersonDTO to create a new case and a new person related to it.")
	@ApiResponse(responseCode = "200", description = "Returns the uploaded entry.", useReturnTypeSchema = true)
	@Path("/pushWithPerson")
	public CoreAndPersonDto<ContactDto> postContact(
		@RequestBody(
			description = "CoreAndPersonDto containing the PersonDto and the ContactDto that are to be added to the existing contact data entries.",
			required = true) @Valid CoreAndPersonDto<ContactDto> dto) {
		return FacadeProvider.getContactFacade().save(dto);

	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available contact data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getContactFacade().getAllActiveUuids();
	}

	@GET
	@Path("/archived/{since}")
	@Operation(summary = "Get the unique IDs of all contact data that has been marked as archived for the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getArchivedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getContactFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	@Operation(summary = "Get the unique IDs of all contact data that has been deleted during the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getDeletedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getContactFacade().getDeletedUuidsSince(new Date(since));
	}

	@GET
	@Path("/obsolete/{since}")
	@Operation(summary = "Get the unique IDs of all contact data that has been marked as obsolete for the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getObsoleteUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getContactFacade().getObsoleteUuidsSince(new Date(since));
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of Contacts based on ContactCriteria filter params.")
	@ApiResponse(description = "Returns a page of contacts that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public Page<ContactIndexDto> getIndexList(
		@RequestBody(
			description = "Contacts based query-filter criteria with sorting proprerty.") CriteriaWithSorting<ContactCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(description = "page offset", required = true) int offset,
		@QueryParam("size") @Parameter(description = "page size", required = true) int size) {
		return FacadeProvider.getContactFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/detailedIndexList")
	@Operation(summary = "Get a page of Contacts with additional personal information based on ContactCriteria filter params.")
	@ApiResponse(description = "Returns a page of contacts with additional personal information that met the filter criteria.",
		responseCode = "200",
		useReturnTypeSchema = true)
	public Page<ContactIndexDetailedDto> getIndexDetailedList(
		@RequestBody(
			description = "Contacts based query-filter criteria with sorting proprerty.") CriteriaWithSorting<ContactCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(description = "page offset", required = true) int offset,
		@QueryParam("size") @Parameter(description = "page size", required = true) int size) {
		return FacadeProvider.getContactFacade()
			.getIndexDetailedPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/externalData")
	@Operation(summary = "Submit a list of ExternalData objects to update the external data entries in the corresponding contacts")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public Response updateExternalData(
		@RequestBody(
			description = "List of ExternalDataDtos containing the updated data about the external reporting tool") @Valid List<ExternalDataDto> externalData) {
		try {
			FacadeProvider.getContactFacade().updateExternalData(externalData);
			return Response.status(Response.Status.OK).build();
		} catch (ExternalDataUpdateException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path("/delete")
	@Operation(summary = "Delete contact data entries from storage based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of UUIDs of the deleted centact entries with the deletion reason \"Deleted via ReST call\".",
		useReturnTypeSchema = true)
	public List<String> delete(@RequestBody(description = "List of UUIDs used to delete contact data entries.") List<String> uuids) {
		return FacadeProvider.getContactFacade().deleteContacts(uuids, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"));
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a single contact based on its universally unique ID (UUID).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Returns a contact for the given UUID.", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "204", description = "Contact with the given UUID cannot be found.", useReturnTypeSchema = false) })
	public ContactDto getByUuid(@Parameter(required = true, description = "UUID of contact.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getContactFacade().getByUuid(uuid);
	}

}
