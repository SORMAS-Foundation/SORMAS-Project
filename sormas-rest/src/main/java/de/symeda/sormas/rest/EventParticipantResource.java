package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/eventparticipants")
@Produces({
	MediaType.APPLICATION_JSON + "; charset=UTF-8"
	})
public class EventParticipantResource {

	@GET 
	@Path("/all/{user}/{since}")
	public List<EventParticipantDto> getAllEventParticipantsAfter(@PathParam("user") String userUuid, @PathParam("since") long since) {
		List<EventParticipantDto> result = FacadeProvider.getEventParticipantFacade().getAllEventParticipantsAfter(new Date(since), userUuid);
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
}
