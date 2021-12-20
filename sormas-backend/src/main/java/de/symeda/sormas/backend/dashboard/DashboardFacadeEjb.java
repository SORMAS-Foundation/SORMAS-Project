package de.symeda.sormas.backend.dashboard;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.dashboard.DashboardFacade;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;

@Stateless(name = "DashboardFacade")
public class DashboardFacadeEjb implements DashboardFacade {

	@EJB
	private EventFacadeEjb.EventFacadeEjbLocal eventFacade;

	@EJB
	private OutbreakFacadeEjb.OutbreakFacadeEjbLocal outbreakFacade;

	@EJB
	private DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;

	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;

	@EJB
	private DashboardService dashboardService;

	@Override
	public List<DashboardCaseDto> getCases(DashboardCriteria dashboardCriteria) {
		return dashboardService.getCases(dashboardCriteria);
	}

	@Override
	public Map<CaseClassification, Integer> getCasesCountByClassification(DashboardCriteria dashboardCriteria) {
		return dashboardService.getCasesCountByClassification(dashboardCriteria);
	}

	@Override
	public String getLastReportedDistrictName(DashboardCriteria dashboardCriteria) {
		return dashboardService.getLastReportedDistrictName(dashboardCriteria);
	}

	@Override
	public Map<PathogenTestResultType, Long> getTestResultCountByResultType(List<DashboardCaseDto> cases) {
		if (cases.isEmpty()) {
			return Collections.emptyMap();
		}
		return sampleFacade.getNewTestResultCountByResultType(cases.stream().map(DashboardCaseDto::getId).collect(Collectors.toList()));
	}

	public Map<PathogenTestResultType, Long> getTestResultCountByResultType(DashboardCriteria dashboardCriteria) {
		return getTestResultCountByResultType(getCases(dashboardCriteria));
	}

	@Override
	public long countCasesConvertedFromContacts(DashboardCriteria dashboardCriteria) {
		return dashboardService.countCasesConvertedFromContacts(dashboardCriteria);
	}

	@Override
	public Map<PresentCondition, Integer> getCasesCountPerPersonCondition(DashboardCriteria dashboardCriteria) {
		return dashboardService.getCasesCountPerPersonCondition(dashboardCriteria);
	}

	@Override
	public List<DashboardEventDto> getNewEvents(DashboardCriteria dashboardCriteria) {
		return dashboardService.getNewEvents(dashboardCriteria);
	}

	@Override
	public Map<EventStatus, Long> getEventCountByStatus(DashboardCriteria dashboardCriteria) {
		return dashboardService.getEventCountByStatus(dashboardCriteria);
	}

	@Override
	public List<DiseaseBurdenDto> getDiseaseBurden(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		Date fromDate,
		Date toDate,
		Date previousFromDate,
		Date previousToDate,
		CriteriaDateType newCaseDateType) {

		//diseases
		List<Disease> diseases = diseaseConfigurationFacade.getAllDiseases(true, true, true);

		//new cases
		DashboardCriteria dashboardCriteria =
			new DashboardCriteria().region(region).district(district).newCaseDateType(newCaseDateType).dateBetween(fromDate, toDate);
		Map<Disease, Long> newCases = dashboardService.getCaseCountByDisease(dashboardCriteria);

		//events
		Map<Disease, Long> events = eventFacade
			.getEventCountByDisease(new EventCriteria().region(region).district(district).eventDateType(null).eventDateBetween(fromDate, toDate));

		//outbreaks
		Map<Disease, Long> outbreakDistrictsCount;
		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.OUTBREAKS)) {
			outbreakDistrictsCount = outbreakFacade
				.getOutbreakDistrictCountByDisease(new OutbreakCriteria().region(region).district(district).reportedBetween(fromDate, toDate));
		} else {
			outbreakDistrictsCount = new HashMap<>();
		}

		//last report district
		Map<Disease, District> lastReportedDistricts = dashboardService.getLastReportedDistrictByDisease(dashboardCriteria);

		//case fatalities
		Map<Disease, Long> caseFatalities = dashboardService.getDeathCountByDisease(dashboardCriteria);

		//previous cases
		dashboardCriteria.dateBetween(previousFromDate, previousToDate);
		Map<Disease, Long> previousCases = dashboardService.getCaseCountByDisease(dashboardCriteria);

		//build diseasesBurden
		List<DiseaseBurdenDto> diseasesBurden = diseases.stream().map(disease -> {
			Long caseCount = newCases.getOrDefault(disease, 0L);
			Long previousCaseCount = previousCases.getOrDefault(disease, 0L);
			Long eventCount = events.getOrDefault(disease, 0L);
			Long outbreakDistrictCount = outbreakDistrictsCount.getOrDefault(disease, 0L);
			Long caseFatalityCount = caseFatalities.getOrDefault(disease, 0L);
			District lastReportedDistrict = lastReportedDistricts.getOrDefault(disease, null);

			String lastReportedDistrictName = lastReportedDistrict == null ? "" : lastReportedDistrict.getName();

			return new DiseaseBurdenDto(
				disease,
				caseCount,
				previousCaseCount,
				eventCount,
				outbreakDistrictCount,
				caseFatalityCount,
				lastReportedDistrictName);

		}).collect(Collectors.toList());

		return diseasesBurden;
	}

	@LocalBean
	@Stateless
	public static class DashboardFacadeEjbLocal extends DashboardFacadeEjb {

	}
}
