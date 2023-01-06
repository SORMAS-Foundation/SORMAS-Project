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
import de.symeda.sormas.api.event.EventParticipantCriteria;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantIndexDto;

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
 */
@Path("/eventparticipants")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Event Participant Resource",
	description = "Management of participants in **Events** that may have had a **Contact** with the source **Case** or secondary contacts with other event participants.\n\n"
		+ "See also: **EventResource**, **EventGroupResource**.")
public class EventParticipantResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all event participants from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of event participants for the given interval.", useReturnTypeSchema = true)
	public List<EventParticipantDto> getAllEventParticipantsAfter(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		List<EventParticipantDto> result = FacadeProvider.getEventParticipantFacade().getAllAfter(new Date(since));
		return result;
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of event participants that fulfill certain criteria.",
		description = "**-** participant entries are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of event participants for the given interval that met the criteria.",
		useReturnTypeSchema = true)
	public List<EventParticipantDto> getAllEventParticipantsAfter(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		List<EventParticipantDto> result = FacadeProvider.getEventParticipantFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
		return result;
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a specific event participants based on his/her unique ID (UUID).")
	@ApiResponse(responseCode = "200", description = "Returns an event participant by his/her UUID.", useReturnTypeSchema = true)
	public EventParticipantDto getByUuid(
		@Parameter(required = true, description = "Universally unique identifier to query the event participant.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getEventParticipantFacade().getByUuid(uuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get event participants based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of event participants by their UUIDs.", useReturnTypeSchema = true)
	public List<EventParticipantDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query event participant entries.", required = true) List<String> uuids) {
		List<EventParticipantDto> result = FacadeProvider.getEventParticipantFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/query/events")
	@Operation(summary = "Get event participants based on their corresponding events' unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of event participants by their parent events' UUIDs.",
		useReturnTypeSchema = true)
	public List<EventParticipantDto> getByEventUuids(
		@RequestBody(description = "List of parent event UUIDs used to query event participant entries.", required = true) List<String> uuids) {
		List<EventParticipantDto> result = FacadeProvider.getEventParticipantFacade().getByEventUuids(uuids);
		return result;
	}

	@POST
	@Path("/query/persons")
	@Operation(summary = "Get event participants based on their corresponding person' unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of event participants by their person-based UUIDs.", useReturnTypeSchema = true)
	public List<EventParticipantDto> getByPersonUuids(
		@RequestBody(description = "List of person-based UUIDs used to query event participant entries.", required = true) List<String> uuids) {
		return FacadeProvider.getEventParticipantFacade().getByPersonUuids(uuids);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of event participants to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded event participant.",
		useReturnTypeSchema = true)
	public List<PushResult> postEventParticipants(
		@RequestBody(description = "List of EventParticipantDtos to be added to the server.",
			required = true) @Valid List<EventParticipantDto> dtos) {

		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getEventParticipantFacade()::save);
		return result;
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available event participants.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getEventParticipantFacade().getAllActiveUuids();
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of EventParticipantIndexDtos based on EventParticipantCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a page of event participants that have met the filter criteria.",
		useReturnTypeSchema = true)
	public Page<EventParticipantIndexDto> getIndexList(
		@RequestBody(description = "Query-filter for event participants with sorting property.",
			required = true) CriteriaWithSorting<EventParticipantCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getEventParticipantFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/archived/{since}")
	@Operation(summary = "Get all archived event participants from a date in the past until now.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of UUIDs of archived event participant entries for the given interval.",
		useReturnTypeSchema = true)
	public List<String> getArchivedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getEventParticipantFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	@Operation(summary = "Get all deleted event participants from a date in the past until now.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of UUIDs of deleted event participant entries for the given interval.",
		useReturnTypeSchema = true)
	public List<String> getDeletedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getEventParticipantFacade().getDeletedUuidsSince(new Date(since));
	}

	@GET
	@Path("/obsolete/{since}")
	@Operation(summary = "Get all obsolete event participants from a date in the past until now.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of UUIDs of obsolete event participant entries for the given interval.",
		useReturnTypeSchema = true)
	public List<String> getObsoleteUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getEventParticipantFacade().getObsoleteUuidsSince(new Date(since));
	}
}
