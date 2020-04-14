package de.symeda.sormas.rest;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.visit.ExternalVisitDto;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/visits-external")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("REST_EXTERNAL_VISITS_USER")
public class ExternalVisitsResource extends EntityDtoResource {

    public final static String EXTERNAL_VISITS_MINIMUM_REQUIRED_VERSION = "1.37.0";

    @GET
    @Path("/contact/{contactUuid}/validity")
    public Boolean isValidContactUuid(@PathParam("contactUuid") String contactUuid) {
        return FacadeProvider.getContactFacade().isValidContactUuid(contactUuid);
    }

    @POST
    @Path("/push")
    public List<PushResult> postExternalVisits(List<ExternalVisitDto> dtos) {
        List<PushResult> result = savePushedDto(dtos, FacadeProvider.getVisitFacade()::saveExternalVisit);
        return result;
    }

    @GET
    @Path("/version")
    public String getVersion() {
        return InfoProvider.get().getVersion();
    }

    @GET
    @Path("/checkcompatibility")
    public CompatibilityCheckResponse isCompatibleToApi(@QueryParam("appVersion") String appVersion) {
        return InfoProvider.get().isCompatibleToApiForMinVersion(appVersion, EXTERNAL_VISITS_MINIMUM_REQUIRED_VERSION);
    }

    @Override
    protected <T> String createErrorMessage(T dto) {
        final ExternalVisitDto externalVisitDto = (ExternalVisitDto) dto;
        return dto.getClass().getSimpleName() + " #contactUUID: " + externalVisitDto.getContactUuid() + "\n";
    }
}
