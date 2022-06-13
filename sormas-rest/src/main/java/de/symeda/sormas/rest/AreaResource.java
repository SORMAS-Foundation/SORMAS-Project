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
import de.symeda.sormas.api.infrastructure.area.AreaDto;

@Path("/areas")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class AreaResource {

	@GET
	@Path("/all/{since}")
	public List<AreaDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getAreaFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<AreaDto> getByUuids(List<String> uuids) {
		List<AreaDto> result = FacadeProvider.getAreaFacade().getByUuids(uuids);
		return result;
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getAreaFacade().getAllUuids();
	}
}
