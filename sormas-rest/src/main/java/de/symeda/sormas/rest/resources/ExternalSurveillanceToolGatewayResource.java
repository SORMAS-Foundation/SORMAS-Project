package de.symeda.sormas.rest.resources;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/external-surveillance")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "External Surveillance Resource",
	description = "Management of external surveillance data. Handles import of cases and events from external sources.")
public class ExternalSurveillanceToolGatewayResource extends EntityDtoResource {

	@POST
	@Path("/share/cases")
	@Operation(summary = "Submit a list of case UUIDs to the server.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public void importCases(
		@RequestBody(description = "List of String with case UUIDs to be imported.", required = true) @Valid List<String> caseUuids) {
		FacadeProvider.getExternalSurveillanceToolFacade().createCaseShareInfo(caseUuids);
	}

	@POST
	@Path("/share/events")
	@Operation(summary = "Submit a list of event UUIDs to the server.")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public void importEvents(
		@RequestBody(description = "List of String with event UUIDs to be imported.", required = true) @Valid List<String> eventUuids) {
		FacadeProvider.getExternalSurveillanceToolFacade().createEventShareInfo(eventUuids);
	}

}
