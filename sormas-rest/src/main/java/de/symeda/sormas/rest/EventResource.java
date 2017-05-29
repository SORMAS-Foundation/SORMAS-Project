package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;

@Path("/events")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class EventResource {

	@GET
	@Path("/all/{user}/{since}")
	public List<EventDto> getAllEvents(@PathParam("user") String userUuid, @PathParam("since") long since) {
		
		List<EventDto> events = FacadeProvider.getEventFacade().getAllEventsAfter(new Date(since), userUuid);
		return events;
	}
	
	@POST
	@Path("/push")
	public Long postEvents(List<EventDto> dtos) {
		EventFacade eventFacade = FacadeProvider.getEventFacade();
		for(EventDto dto : dtos) {
			eventFacade.saveEvent(dto);
		}
		
		return new Date().getTime();
	}
	
}
