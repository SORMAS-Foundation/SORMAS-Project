package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.ContinentDto;

@Path("/continents")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class ContinentResource {

	@GET
	@Path("/all/{since}")
	public List<ContinentDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.geContinentFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<ContinentDto> getByUuids(List<String> uuids) {
		return FacadeProvider.geContinentFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.geContinentFacade().getAllUuids();
	}
}
