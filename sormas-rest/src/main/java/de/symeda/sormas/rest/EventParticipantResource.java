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
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/eventparticipants")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class EventParticipantResource {

	@GET 
	@Path("/all/{since}")
	public List<EventParticipantDto> getAllEventParticipantsAfter(@Context SecurityContext sc, @PathParam("since") long since) {

		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<EventParticipantDto> result = FacadeProvider.getEventParticipantFacade().getAllEventParticipantsAfter(new Date(since), userDto.getUuid());
		return result;
	}
	
	@POST
	@Path("/query")
	public List<EventParticipantDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {

		List<EventParticipantDto> result = FacadeProvider.getEventParticipantFacade().getByUuids(uuids); 
		return result;
	}
	
	@POST @Path("/push")
	public Integer postEventParticipants(List<EventParticipantDto> dtos) {
		
		EventParticipantFacade eventParticipantFacade = FacadeProvider.getEventParticipantFacade();
		for (EventParticipantDto dto : dtos) {
			eventParticipantFacade.saveEventParticipant(dto);
		}
		
		return dtos.size();
	}
	
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getEventParticipantFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
}
