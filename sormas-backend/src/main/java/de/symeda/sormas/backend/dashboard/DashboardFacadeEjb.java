package de.symeda.sormas.backend.dashboard;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.dashboard.DashboardFacade;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;

@Stateless(name = "DashboardFacade")
public class DashboardFacadeEjb implements DashboardFacade {

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
	public List<DashboardQuarantineDataDto> getQuarantineData(DashboardCriteria dashboardCriteria) {
		return dashboardService.getQuarantineData(dashboardCriteria);
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

	@Override
	public long countCasesConvertedFromContacts(DashboardCriteria dashboardCriteria) {
		return dashboardService.countCasesConvertedFromContacts(dashboardCriteria);
	}

	@Override
	public Map<PresentCondition, Integer> getCasesCountPerPersonCondition(DashboardCriteria dashboardCriteria) {
		return dashboardService.getCasesCountPerPersonCondition(dashboardCriteria);
	}

	@Override
	public List<DashboardEventDto> getNewEvents(EventCriteria eventCriteria) {
		return dashboardService.getNewEvents(eventCriteria);
	}

	@Override
	public Map<EventStatus, Long> getEventCountByStatus(EventCriteria eventCriteria) {
		return dashboardService.getEventCountByStatus(eventCriteria);
	}

	@LocalBean
	@Stateless
	public static class DashboardFacadeEjbLocal extends DashboardFacadeEjb {

	}
}
