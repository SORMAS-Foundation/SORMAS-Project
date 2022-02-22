package de.symeda.sormas.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;


@Path("/external-surveillance")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ExternalSurveillanceToolGatewayResource extends EntityDtoResource {

	@POST
	@Path("/share/cases")
	public void importCases(@Valid List<String> caseUuids) {
		FacadeProvider.getExternalSurveillanceToolFacade().createCaseShareInfo(caseUuids);
	}

	@POST
	@Path("/share/events")
	public void importEvents(@Valid List<String> eventUuids) {
		FacadeProvider.getExternalSurveillanceToolFacade().createEventShareInfo(eventUuids);
	}

}
