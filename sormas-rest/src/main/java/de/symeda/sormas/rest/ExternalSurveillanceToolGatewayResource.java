package de.symeda.sormas.rest;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.event.EventDto;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/external-surveillance")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
        "USER",
        "REST_USER" })
public class ExternalSurveillanceToolGatewayResource extends EntityDtoResource {

    @POST
    @Path("/import/cases")
    public List<PushResult> importCases(@Valid List<CaseDataDto> dtos) {
        List<PushResult> result = savePushedDto(dtos, FacadeProvider.getCaseFacade()::saveCase);
        FacadeProvider.getExternalSurveillanceToolFacade().createCaseShareInfo(dtos);
        return result;
    }

    @POST
    @Path("/import/events")
    public List<PushResult> importEvents(@Valid List<EventDto> dtos) {
        List<PushResult> result = savePushedDto(dtos, FacadeProvider.getEventFacade()::saveEvent);
        FacadeProvider.getExternalSurveillanceToolFacade().createEventShareInfo(dtos);
        return result;
    }

}
