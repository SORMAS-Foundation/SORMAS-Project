package de.symeda.sormas.api.dashboard;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.NewCaseDateType;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;

@Remote
public interface DashboardFacade {

	List<DashboardCaseDto> getCases(DashboardCriteria dashboardCriteria);

	Map<CaseClassification, Integer> getCasesCountByClassification(DashboardCriteria dashboardCriteria);

	String getLastReportedDistrictName(DashboardCriteria dashboardCriteria);

	Map<PathogenTestResultType, Long> getTestResultCountByResultType(List<DashboardCaseDto> cases);

	long countCasesConvertedFromContacts(DashboardCriteria dashboardCriteria);

	Map<PresentCondition, Integer> getCasesCountPerPersonCondition(DashboardCriteria dashboardCriteria);

	List<DashboardEventDto> getNewEvents(DashboardCriteria dashboardCriteria);

	Map<EventStatus, Long> getEventCountByStatus(DashboardCriteria dashboardCriteria);

	List<DiseaseBurdenDto> getDiseaseBurden(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		Date fromDate,
		Date toDate,
		Date previousFromDate,
		Date previousToDate,
		NewCaseDateType newCaseDateType);
}
