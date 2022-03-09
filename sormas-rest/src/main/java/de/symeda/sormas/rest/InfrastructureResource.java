package de.symeda.sormas.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;

@Path("/infrastructure")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class InfrastructureResource {

	@POST
	@Path("/sync")
	public InfrastructureSyncDto getInfrastructureSyncData(InfrastructureChangeDatesDto changeDates) {
		return FacadeProvider.getInfrastructureSyncFacade().getInfrastructureSyncData(changeDates);
	}
}
