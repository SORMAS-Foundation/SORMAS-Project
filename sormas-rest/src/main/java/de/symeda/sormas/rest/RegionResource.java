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
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.user.UserReferenceDto;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/regions")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class RegionResource {

	@GET @Path("/all/{since}")
	public List<RegionDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getRegionFacade().getAllAfter(new Date(since));
	}	
	
	@POST
	@Path("/query")
	public List<RegionDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {

		List<RegionDto> result = FacadeProvider.getRegionFacade().getByUuids(uuids); 
		return result;
	}
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getRegionFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
}
