package de.symeda.sormas.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/eventGroups")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class EventGroupResource extends EntityDtoResource {

	/**
	 * This method returns the eventGroupDto that correspond to the given uuid.
	 * 
	 * @param uuid
	 * @return eventGroupDto
	 */
	@GET
	@Path("/{uuid}")
	public EventGroupDto getByUuid(@PathParam("uuid") String uuid) {
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
	public Page<EventGroupIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<EventGroupCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getEventGroupFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@DELETE
	@Path("/{uuid}")
	public void delete(@PathParam("uuid") String uuid) {
		FacadeProvider.getEventGroupFacade().deleteEventGroup(uuid);
	}

	@POST
	@Path("/push")
	public EventGroupDto postEventGroup(@RequestBody EventGroupDto eventGroupDto) {
		return FacadeProvider.getEventGroupFacade().saveEventGroup(eventGroupDto);
	}

	@POST
	@Path("/{uuid}/linkEvents")
	public void linkEventsToGroup(@PathParam("uuid") String uuid, @RequestBody List<EventReferenceDto> events) {
		FacadeProvider.getEventGroupFacade().linkEventsToGroup(events, new EventGroupReferenceDto(uuid));
	}

	@POST
	@Path("/{uuid}/unlinkEvent")
	public void unlinkEventGroup(@PathParam("uuid") String uuid, @QueryParam("eventUuid") String eventUuid) {
		FacadeProvider.getEventGroupFacade().unlinkEventGroup(new EventReferenceDto(eventUuid), new EventGroupReferenceDto(uuid));
	}

}
