package de.symeda.sormas.api.dashboard;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;

@Remote
public interface DashboardFacade {

	List<DashboardCaseDto> getCases(CaseCriteria caseCriteria);

	Map<CaseClassification, Integer> getCasesCountByClassification(CaseCriteria caseCriteria);

	List<DashboardQuarantineDataDto> getQuarantineData(CaseCriteria caseCriteria);

	String getLastReportedDistrictName(CaseCriteria caseCriteria);

	Map<PathogenTestResultType, Long> getTestResultCountByResultType(List<DashboardCaseDto> cases);

	long countCasesConvertedFromContacts(CaseCriteria caseCriteria);
}
