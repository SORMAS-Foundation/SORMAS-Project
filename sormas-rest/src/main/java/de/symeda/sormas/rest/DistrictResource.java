package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.user.UserReferenceDto;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/districts")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class DistrictResource {

	@GET @Path("/all/{since}")
	public List<DistrictDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getDistrictFacade().getAllAfter(new Date(since));
	}	
	
	@POST
	@Path("/query")
	public List<DistrictDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {

		List<DistrictDto> result = FacadeProvider.getDistrictFacade().getByUuids(uuids); 
		return result;
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getDistrictFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
}
