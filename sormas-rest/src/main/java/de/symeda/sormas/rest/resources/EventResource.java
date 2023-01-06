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
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Event Resource",
	description = "Management of events that may need to be tracked as they encourage the spread of the disease. "
		+ "Events usually feature a source **Case** from which further **Contacts** (or escalated to cases) can be derived.\n\n"
		+ "See also: **EventParticipantResource**, **EventGroupResource**.")

public class EventResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all available events from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of events for the given interval.", useReturnTypeSchema = true)
	public List<EventDto> getAllEvents(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of events that fulfill certain criteria.",
		description = "**-** events are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of events for the given interval that met the criteria.",
		useReturnTypeSchema = true)
	public List<EventDto> getAllEvents(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getEventFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
	}

	/**
	 * This method returns the eventDto that correspond to the given uuid.
	 * The return eventDto has the superordinateEvent of type EventDetailedReferenceDto.
	 * 
	 * @param uuid
	 * @return
	 *         The return eventDto has the superordinateEvent of type EventDetailedReferenceDto
	 */
	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a specific event based on its unique ID (UUID).")
	@ApiResponse(responseCode = "200", description = "Returns an event by its UUID.", useReturnTypeSchema = true)
	public EventDto getByUuid(
		@Parameter(required = true, description = "Universally unique identifier to query the event.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getEventFacade().getEventByUuid(uuid, true);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get a list of events based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of customizable enum values by their UUIDs.", useReturnTypeSchema = true)
	public List<EventDto> getByUuids(@RequestBody(required = true, description = "List of UUIDs used to query event entries.") List<String> uuids) {
		List<EventDto> result = FacadeProvider.getEventFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of events to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded event.",
		useReturnTypeSchema = true)
	public List<PushResult> postEvents(
		@RequestBody(description = "List of EventDtos to be added to the server.", required = true) @Valid List<EventDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getEventFacade()::save);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available event entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getEventFacade().getAllActiveUuids();
	}

	@GET
	@Path("/archived/{since}")
	@Operation(summary = "Get all archived events from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of UUIDs of archived events for the given interval.", useReturnTypeSchema = true)
	public List<String> getArchivedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	@Operation(summary = "Get all deleted events from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of UUIDs of deleted events for the given interval.", useReturnTypeSchema = true)
	public List<String> getDeletedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getDeletedUuidsSince(new Date(since));
	}

	@GET
	@Path("/obsolete/{since}")
	@Operation(summary = "Get all obsolete events from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of UUIDs of events tasks for the given interval.", useReturnTypeSchema = true)
	public List<String> getObsoleteUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getObsoleteUuidsSince(new Date(since));
	}

	/**
	 * 
	 * @param criteriaWithSorting
	 *            - The criteria object inside criteriaWithSorting cannot be null. Use an empty criteria instead.
	 * @param offset
	 * @param size
	 * @return
	 */
	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of EventIndexDtos based on EventCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of events that have met the filter criteria.", useReturnTypeSchema = true)
	public Page<EventIndexDto> getIndexList(
		@RequestBody(description = "Event-based query-filter with sorting property.",
			required = true) CriteriaWithSorting<EventCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getEventFacade().getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/externalData")
	@Operation(summary = "Submit a list of ExternalData objects to update the external data entries in the corresponding events")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public Response updateExternalData(
		@RequestBody(
			description = "List of ExternalDataDtos containing the updated data about the external reporting tool") List<ExternalDataDto> externalData) {
		try {
			FacadeProvider.getEventFacade().updateExternalData(externalData);
			return Response.status(Response.Status.OK).build();
		} catch (ExternalDataUpdateException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path("/delete")
	@Operation(summary = "Delete events based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of UUIDs of events for which deletion was successful.",
		useReturnTypeSchema = true)
	public List<String> delete(@RequestBody(description = "List of UUIDs denoting the events to be deleted.", required = true) List<String> uuids) {
		return FacadeProvider.getEventFacade().deleteEvents(uuids, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"));
	}

	@POST
	@Path("/children")
	@Operation(summary = "Get all events that are subordinate events based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of UUIDs of subordinate events.", useReturnTypeSchema = true)
	public List<String> getChildrenUuids(
		@RequestBody(description = "List of UUIDs used to query the subordinate event entries.", required = true) List<String> uuids) {
		return FacadeProvider.getEventFacade().getSubordinateEventUuids(uuids);
	}

	@GET
	@Path("/specificEvent/{searchTerm}")
	@Operation(summary = "Search for a specific event based on the UUID of a connected case or person.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Returns the UUID of an event that met the search term.", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "204", description = "No event was found for the provided search term.", useReturnTypeSchema = false) })
	public String getSpecificCase(
		@Parameter(required = true,
			description = "UUID of a case connected to the event or of a person attending the event in question.") @PathParam("searchTerm") String searchTerm) {
		return FacadeProvider.getEventFacade().getUuidByCaseUuidOrPersonUuid(searchTerm);
	}
}
