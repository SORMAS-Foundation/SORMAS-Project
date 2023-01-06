package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.dashboard.DashboardCaseMeasureDto;
import de.symeda.sormas.api.dashboard.DashboardCaseStatisticDto;
import de.symeda.sormas.api.dashboard.DashboardContactStatisticDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.dashboard.SurveillanceDashboardCriteria;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Dashboard Resource", description = "Management of data displayed in the dashboard.")
public class DashboardResource extends EntityDtoResource {

	@POST
	@Path("/diseaseBurden")
	@Operation(summary = "Get the current disease burden based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns information about the disease burden.", useReturnTypeSchema = true)
	public List<DiseaseBurdenDto> getAll(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria criteria) {
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
	@Operation(summary = "Get statistical information about cases based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns statistical information about cases.", useReturnTypeSchema = true)
	public DashboardCaseStatisticDto getDashboardCaseStatistic(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getDashboardCaseStatistic(dashboardCriteria);
	}

	@POST
	@Path("/newEvents")
	@Operation(summary = "Get all types of events and how many of each type where created based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map of all event types and the quantity of each event type.",
		useReturnTypeSchema = true)
	public Map<EventStatus, Long> getEventCountByStatus(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEventCountByStatus(dashboardCriteria);
	}

	@POST
	@Path("/testResults")
	@Operation(summary = "Get all types of pathogen test results and how many of each type where created based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map of all pathogen test result types and the quantity of each test result.",
		useReturnTypeSchema = true)
	public Map<PathogenTestResultType, Long> getTestResultCountByResultType(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getTestResultCountByResultType(dashboardCriteria);
	}

	@POST
	@Path("/epiCurveDataPerCaseClassification")
	@Operation(
		summary = "Get all case classification types and how many cases where classified as each type based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map of all case classification types and the quantity of accordingly classified cases.",
		useReturnTypeSchema = true)
	public Map<Date, Map<CaseClassification, Integer>> getEpidemiologicalCurveDataPerCaseClassification(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEpiCurveSeriesElementsPerCaseClassification(dashboardCriteria);
	}

	@POST
	@Path("/epiCurveDataPerPresentCondition")
	@Operation(
		summary = "Get all types of present conditions a person can be in and how many persons are in each contidion based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map of all present condition types and the quantity of accordingly classified persons.",
		useReturnTypeSchema = true)
	public Map<Date, Map<PresentCondition, Integer>> getEpidemiologicalCurveDataPerPresentCondition(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEpiCurveSeriesElementsPerPresentCondition(dashboardCriteria);
	}

	@POST
	@Path("/epiCurveElementsContactClassification")
	@Operation(
		summary = "Get all contact classification types and how many contacts where classified as each type based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map of all contact classification types and the quantity of accordingly classified contacts.",
		useReturnTypeSchema = true)
	public Map<Date, Map<ContactClassification, Long>> getEpiCurveSeriesElementsPerContactClassification(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEpiCurveSeriesElementsPerContactClassification(dashboardCriteria);
	}

	@POST
	@Path("/epiCurveElementsContactFollowUpStatus")
	@Operation(
		summary = "Get all contact follow-up/visit status types and how many follow-ups/visits where classified as each type based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map containing each date within the primary time period and all follow-up/visit status types with the quantity of accordingly classified follow-ups/visits for each date.",
		useReturnTypeSchema = true)
	public Map<Date, Map<String, Long>> getEpiCurveSeriesElementsPerContactFollowUpStatus(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEpiCurveSeriesElementsPerContactFollowUpStatus(dashboardCriteria);
	}

	@POST
	@Path("/epiCurveElementsContactFollowUpUntil")
	@Operation(summary = "Get a number of contacts that have to be followed up on based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200",
		description = "Returns a map conaining each date within the primary time period an the number of follow-ups/visits that have to be done until the date.",
		useReturnTypeSchema = true)
	public Map<Date, Integer> getEpiCurveSeriesElementsPerContactFollowUpUntil(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getEpiCurveSeriesElementsPerContactFollowUpUntil(dashboardCriteria);
	}

	@POST
	@Path("/caseMeasurePerDistrict")
	@Operation(summary = "Get the measure of cases (number of cases divided by DistrictDto.CASE_INCIDENCE_DIVISOR) on a district by district bases.")
	@ApiResponse(responseCode = "200", description = "Returns the case measure for all districts", useReturnTypeSchema = true)
	public DashboardCaseMeasureDto getCaseMeasurePerDistrict(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getCaseMeasurePerDistrict(dashboardCriteria);
	}

	@POST
	@Path("/loadMapCaseData")
	@Operation(
		summary = "Get a list of objects each containing geodetic location data related to a case for every case that fulfills DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a list of MapCaseDtos that met the filter criteria.", useReturnTypeSchema = true)
	public List<MapCaseDto> getMapCaseData(
		@RequestBody(required = true,
			description = "Criteria for data that should be shown in the surveillance part of the dashboard") SurveillanceDashboardCriteria dashboardCriteria) {
		return FacadeProvider.getCaseFacade()
			.getCasesForMap(
				dashboardCriteria.getRegion(),
				dashboardCriteria.getDistrict(),
				dashboardCriteria.getDisease(),
				dashboardCriteria.getDateFrom(),
				dashboardCriteria.getDateTo(),
				dashboardCriteria.getNewCaseDateType());
	}

	@POST
	@Path("/loadMapContactData")
	@Operation(
		summary = "Get a list of objects each containing geodetic location data related to a contact for every contact that fulfills DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a list of MapContactDtos that met the filter criteria.", useReturnTypeSchema = true)
	public List<MapContactDto> getMapContactData(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getContactFacade()
			.getContactsForMap(
				dashboardCriteria.getRegion(),
				dashboardCriteria.getDistrict(),
				dashboardCriteria.getDisease(),
				dashboardCriteria.getDateFrom(),
				dashboardCriteria.getDateTo());
	}

	@POST
	@Path("/loadMapEventData")
	@Operation(
		summary = "Get a list of objects each containing data related to an event for every event that fulfills DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a list of MapContactDtos that met the filter criteria.", useReturnTypeSchema = true)
	public List<DashboardEventDto> getMapEventData(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getNewEvents(dashboardCriteria);
	}

	@POST
	@Path("/contacts")
	@Operation(summary = "Get statistical information about contacts based on DashboardCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns statistical information about contacts.", useReturnTypeSchema = true)
	public DashboardContactStatisticDto getDashboardContactStatistic(
		@RequestBody(required = true, description = "Criteria for data that should be shown in the dashboard") DashboardCriteria dashboardCriteria) {
		return FacadeProvider.getDashboardFacade().getDashboardContactStatistic(dashboardCriteria);
	}

}
