package de.symeda.sormas.backend.dashboard;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardFacade;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;

@Stateless(name = "DashboardFacade")
public class DashboardFacadeEjb implements DashboardFacade {

	@EJB
	private SampleFacadeEjb.SampleFacadeEjbLocal sampleFacade;

	@EJB
	private DashboardService dashboardService;

	@Override
	public List<DashboardCaseDto> getCases(CaseCriteria caseCriteria) {
		return dashboardService.getCases(caseCriteria);
	}

	@Override
	public Map<CaseClassification, Integer> getCasesCountByClassification(CaseCriteria caseCriteria) {
		return dashboardService.getCasesCountByClassification(caseCriteria);
	}

	@Override
	public List<DashboardQuarantineDataDto> getQuarantineData(CaseCriteria caseCriteria) {
		return dashboardService.getQuarantineData(caseCriteria);
	}

	@Override
	public String getLastReportedDistrictName(CaseCriteria caseCriteria) {
		return dashboardService.getLastReportedDistrictName(caseCriteria);
	}

	@Override
	public Map<PathogenTestResultType, Long> getTestResultCountByResultType(List<DashboardCaseDto> cases) {
		if (cases.isEmpty()) {
			return Collections.emptyMap();
		}
		return sampleFacade.getNewTestResultCountByResultType(cases.stream().map(DashboardCaseDto::getId).collect(Collectors.toList()));
	}

	@Override
	public long countCasesConvertedFromContacts(CaseCriteria caseCriteria) {
		return dashboardService.countCasesConvertedFromContacts(caseCriteria);
	}

	@LocalBean
	@Stateless
	public static class DashboardFacadeEjbLocal extends DashboardFacadeEjb {

	}
}
