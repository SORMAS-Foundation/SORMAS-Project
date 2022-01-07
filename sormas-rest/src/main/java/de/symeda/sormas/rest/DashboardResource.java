package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.dashboard.DashboardCaseStatisticDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.PresentCondition;
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
	@Path("/newCases")
	public DashboardCaseStatisticDto getDashboardCaseStatistic(@RequestBody DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getDashboardCaseStatistic(dashboardCriteria);
	}

	@POST
	@Path("/newEvents")
	public Map<EventStatus, Long> getEventCountByStatus(@RequestBody DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEventCountByStatus(dashboardCriteria);
	}

	@POST
	@Path("/testResults")
	public Map<PathogenTestResultType, Long> getTestResultCountByResultType(@RequestBody DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getTestResultCountByResultType(dashboardCriteria);
	}

	@POST
	@Path("/epiCurveDataPerCaseClassification")
	public Map<Date, Map<CaseClassification, Integer>> getEpidemiologicalCurveDataPerCaseClassification(
		@RequestBody DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEpiCurveSeriesElementsPerCaseClassification(dashboardCriteria);
	}

	@POST
	@Path("/epiCurveDataPerPresentCondition")
	public Map<Date, Map<PresentCondition, Integer>> getEpidemiologicalCurveDataPerPresentCondition(
		@RequestBody DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEpiCurveSeriesElementsPerPresentCondition(dashboardCriteria);
	}

}
