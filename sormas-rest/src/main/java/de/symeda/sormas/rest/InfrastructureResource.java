package de.symeda.sormas.rest;

import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;

@Path("/infrastructure")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class InfrastructureResource {

	@GET @Path("/all/{since}")
	public InfrastructureSyncDto getNewInfrastructureData(@PathParam("since") long since) {
		return FacadeProvider.getInfrastructureFacade().getNewInfrastructureData(new Date(since));
	}
	
}