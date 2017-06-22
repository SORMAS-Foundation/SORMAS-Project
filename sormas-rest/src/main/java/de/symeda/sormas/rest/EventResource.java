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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

@Path("/events")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class EventResource {

	@GET
	@Path("/all/{since}")
	public List<EventDto> getAllEvents(@Context SecurityContext sc, @PathParam("since") long since) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<EventDto> events = FacadeProvider.getEventFacade().getAllEventsAfter(new Date(since), userDto.getUuid());
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
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getEventFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
}
