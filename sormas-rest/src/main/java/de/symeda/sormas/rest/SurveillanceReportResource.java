package de.symeda.sormas.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;

@Path("/surveillancereports")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class SurveillanceReportResource extends EntityDtoResource {

	@POST
	@Path("/query/cases")
	public List<SurveillanceReportDto> getByCaseUuids(List<String> uuids) {
		List<SurveillanceReportDto> result = FacadeProvider.getSurveillanceReportFacade().getByCaseUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	public List<PushResult> postCaseReports(List<SurveillanceReportDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getSurveillanceReportFacade()::saveSurveillanceReport);
	}
}
