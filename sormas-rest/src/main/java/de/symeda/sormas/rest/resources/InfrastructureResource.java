package de.symeda.sormas.rest.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.infrastructure.InfrastructureChangeDatesDto;
import de.symeda.sormas.api.infrastructure.InfrastructureSyncDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/infrastructure")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Infrastructure Resource", description = "Synchronization of geolocation, user, and disease data between server and client.")
public class InfrastructureResource {

	@POST
	@Path("/sync")
	@Operation(summary = "Get all infrastructure information that has changed since given cut-off dates in the past.")
	@ApiResponse(
		description = "Returns an InfraStructureSyncDto containing lists of geolocation, user, and disease data that changed within the given timespan.",
		responseCode = "200",
		useReturnTypeSchema = true)
	public InfrastructureSyncDto getInfrastructureSyncData(
		@Parameter(
			description = "Dates to request synchronization of all infrastructre-related collections on the server that were modified from that point onward.",
			required = true) InfrastructureChangeDatesDto changeDates) {
		return FacadeProvider.getInfrastructureSyncFacade().getInfrastructureSyncData(changeDates);
	}
}
