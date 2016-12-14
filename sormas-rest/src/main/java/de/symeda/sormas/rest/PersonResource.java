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
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/persons")
@Produces({
	MediaType.APPLICATION_JSON + "; charset=UTF-8"
	})
public class PersonResource {

	@GET @Path("/all/{since}")
	public List<PersonDto> getAllPersons(@PathParam("since") long since) {
		List<PersonDto> result = FacadeProvider.getPersonFacade().getPersonsAfter(new Date(since));
		return result;
	}
	
	@POST @Path("/push")
	public Integer postPersons(List<PersonDto> dtos) {
		
		PersonFacade personFacade = FacadeProvider.getPersonFacade();
		for (PersonDto dto : dtos) {
			personFacade.savePerson(dto);
		}
		
		return dtos.size();
	}
}
