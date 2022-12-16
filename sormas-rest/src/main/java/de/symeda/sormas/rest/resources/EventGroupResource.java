package de.symeda.sormas.rest.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupDto;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/eventGroups")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Event Group Resource",
	description = "Supports simple linking of **Events** to a common group.\n\n" + "See also: **EventResource**, **EventParticipantResource**.")
public class EventGroupResource extends EntityDtoResource {

	/**
	 * This method returns the eventGroupDto that correspond to the given uuid.
	 * 
	 * @param uuid
	 * @return eventGroupDto
	 */
	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a specific event group based on its unique ID (UUID).")
	@ApiResponse(responseCode = "200", description = "Returns an event group by its UUID.", useReturnTypeSchema = true)
	public EventGroupDto getByUuid(
		@Parameter(required = true, description = "Universally unique identifier to query the event group.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getEventGroupFacade().getEventGroupByUuid(uuid);
	}

	/**
	 * @param criteriaWithSorting
	 *            - The criteria object inside criteriaWithSorting cannot be null. Use an empty criteria instead.
	 * @param offset
	 * @param size
	 * @return
	 */
	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of EventGroupIndexDtos based on EventGroupCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of event groups that have met the filter criteria.", useReturnTypeSchema = true)
	public Page<EventGroupIndexDto> getIndexList(
		@RequestBody(description = "Query-filter for event groups with sorting property.",
			required = true) CriteriaWithSorting<EventGroupCriteria> criteriaWithSorting,
		@Parameter(required = true, description = "page offset") @QueryParam("offset") int offset,
		@Parameter(required = true, description = "page size") @QueryParam("size") int size) {
		return FacadeProvider.getEventGroupFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@DELETE
	@Path("/{uuid}")
	@Operation(summary = "Delete an event group based on its unique ID (UUID).",
		description = "Events that were part of the deleted group are **not deleted** themselves.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public void delete(
		@Parameter(required = true,
			description = "Universally unique identifier of the event groups to be deleted.") @PathParam("uuid") String uuid) {
		FacadeProvider.getEventGroupFacade().deleteEventGroup(uuid);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of event groups to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded event group.",
		useReturnTypeSchema = true)
	public EventGroupDto postEventGroup(
		@RequestBody(description = "List of EventGroupDtos to be added to the server.", required = true) EventGroupDto eventGroupDto) {
		return FacadeProvider.getEventGroupFacade().saveEventGroup(eventGroupDto);
	}

	@POST
	@Path("/{uuid}/linkEvents")
	@Operation(summary = "Connect a list of events to a common event group.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public void linkEventsToGroup(
		@Parameter(required = true,
			description = "Universally unique identifier of the event group to link the events to.") @PathParam("uuid") String uuid,
		@RequestBody(description = "List of EventReferenceDtos to be added to the common group.", required = true) List<EventReferenceDto> events) {
		FacadeProvider.getEventGroupFacade().linkEventsToGroup(events, new EventGroupReferenceDto(uuid));
	}

	@POST
	@Path("/{uuid}/unlinkEvent")
	@Operation(summary = "Disconnect a single events from an event group.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public void unlinkEventGroup(
		@Parameter(required = true,
			description = "Universally unique identifier of the event group to remove the event from.") @PathParam("uuid") String uuid,
		@Parameter(required = true,
			description = "Universally unique identifier of the event to be removed from the group.") @QueryParam("eventUuid") String eventUuid) {
		FacadeProvider.getEventGroupFacade().unlinkEventGroup(new EventReferenceDto(eventUuid), new EventGroupReferenceDto(uuid));
	}

}
