package de.symeda.sormas.api.dashboard;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;

@Remote
public interface DashboardFacade {

	List<DashboardCaseDto> getCases(CaseCriteria caseCriteria);

	List<CaseClassification> getCasesCountByClassification(CaseCriteria caseCriteria);

	List<DashboardQuarantineDataDto> getQuarantineData(CaseCriteria caseCriteria);
}
