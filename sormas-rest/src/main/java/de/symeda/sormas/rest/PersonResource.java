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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/persons")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class PersonResource {

	@GET 
	@Path("/all/{since}")
	public List<PersonDto> getAllPersons(@Context SecurityContext sc, @PathParam("since") long since) {

		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<PersonDto> result = FacadeProvider.getPersonFacade().getPersonsAfter(new Date(since), userDto.getUuid());
		return result;
	}
	
	@GET
	@Path("/query")
	@Deprecated
	/**
	 * Used by app before version 0.22.2
	 */
	public List<PersonDto> getByUuidsPre222(@Context SecurityContext sc, @QueryParam("uuids") List<String> uuids) {

		List<PersonDto> result = FacadeProvider.getPersonFacade().getByUuids(uuids); 
		return result;
	}
	
	@POST
	@Path("/query")
	public List<PersonDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {

		List<PersonDto> result = FacadeProvider.getPersonFacade().getByUuids(uuids); 
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
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getPersonFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
}
