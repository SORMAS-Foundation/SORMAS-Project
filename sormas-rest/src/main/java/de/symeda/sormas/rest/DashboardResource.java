package de.symeda.sormas.rest;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class DashboardResource extends EntityDtoResource {

	@POST
	@Path("/diseaseBurden")
	public List<DiseaseBurdenDto> getAll(@RequestBody DashboardCriteria criteria) {
		return FacadeProvider.getDashboardFacade()
			.getDiseaseBurden(
				criteria.getRegion(),
				criteria.getDistrict(),
				criteria.getDateFrom(),
				criteria.getDateTo(),
				criteria.getPreviousDateFrom(),
				criteria.getPreviousDateTo(),
				criteria.getNewCaseDateType());
	}

	@POST
	@Path("/newEvents")
	public Map<EventStatus, Long> getEventCountByStatus(@RequestBody DashboardCriteria criteria) {
		return FacadeProvider.getDashboardFacade().getEventCountByStatus(criteria);
	}

	@POST
	@Path("/testResults")
	public Map<PathogenTestResultType, Long> getTestResultCountByResultType(@RequestBody DashboardCriteria criteria) {
		return FacadeProvider.getDashboardFacade().getTestResultCountByResultType(criteria);
	}

}
