package de.symeda.sormas.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.person.PersonDto;

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

	@GET
	@Path("/{uuid}")
	public PersonDto getByUuid(@PathParam("uuid") String uuid) {
		
		PersonDto dto = FacadeProvider.getPersonFacade().getByUuid(uuid);
		return dto;
	}

	@GET
	@Path("/all")
	public List<PersonDto> getAllPersons() {
		return FacadeProvider.getPersonFacade().getAllPersons();
	}
}
