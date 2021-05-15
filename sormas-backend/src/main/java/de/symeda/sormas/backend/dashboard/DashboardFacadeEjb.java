package de.symeda.sormas.backend.dashboard;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.dashboard.DashboardFacade;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;

@Stateless(name = "DashboardFacade")
public class DashboardFacadeEjb implements DashboardFacade {

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;

	@Override
	public List<DashboardCaseDto> getCases(CaseCriteria caseCriteria) {
		return caseFacade.getCasesForDashboard(caseCriteria);
	}

	@Override
	public List<CaseClassification> getCasesCountByClassification(CaseCriteria caseCriteria) {
		return caseFacade.getCasesCountByClassification(caseCriteria);
	}

	@Override
	public List<DashboardQuarantineDataDto> getQuarantineData(CaseCriteria caseCriteria) {
		return caseFacade.getQuarantineDataForDashBoard(caseCriteria);
	}

	@LocalBean
	@Stateless
	public static class DashboardFacadeEjbLocal extends DashboardFacadeEjb {

	}
}
