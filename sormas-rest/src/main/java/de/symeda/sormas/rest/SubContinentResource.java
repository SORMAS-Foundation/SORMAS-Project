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
import de.symeda.sormas.api.region.SubContinentDto;

@Path("/subcontinents")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class SubContinentResource {

	@GET
	@Path("/all/{since}")
	public List<SubContinentDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.geSubContinentFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<SubContinentDto> getByUuids(List<String> uuids) {
		return FacadeProvider.geSubContinentFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.geSubContinentFacade().getAllUuids();
	}
}
