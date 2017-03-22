package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/contacts")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
public class ContactResource {

	@GET
	@Path("/all/{user}/{since}")
	public List<ContactDto> getAllContacts(@PathParam("user") String userUuid, @PathParam("since") long since) {
		
		List<ContactDto> contacts = FacadeProvider.getContactFacade().getAllContactsAfter(new Date(since), userUuid);
		return contacts;
	}
	
	@POST 
	@Path("/push")
	public Long postContacts(List<ContactDto> dtos) {
		
		// special case: contact's "follow-up until" date might be modified
		boolean contactModified = false;
		
		ContactFacade contactFacade = FacadeProvider.getContactFacade();
		for (ContactDto dto : dtos) {
			ContactDto resultDto = contactFacade.saveContact(dto);
			if (resultDto.getFollowUpUntil() != dto.getFollowUpUntil()) {
				contactModified = true;
			}
		}
		
		// -1 tells the device to pull again
		return contactModified ? -1L : new Date().getTime();
	}
}
