package de.symeda.sormas.api.dashboard;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;

@Remote
public interface DashboardFacade {

	List<DashboardCaseDto> getCases(DashboardCriteria dashboardCriteria);

	Map<CaseClassification, Integer> getCasesCountByClassification(DashboardCriteria dashboardCriteria);

	String getLastReportedDistrictName(DashboardCriteria dashboardCriteria);

	long countCasesConvertedFromContacts(DashboardCriteria dashboardCriteria);

	Map<PresentCondition, Integer> getCasesCountPerPersonCondition(DashboardCriteria dashboardCriteria);

	List<DashboardEventDto> getNewEvents(DashboardCriteria dashboardCriteria);

	Map<EventStatus, Long> getEventCountByStatus(DashboardCriteria dashboardCriteria);

	DashboardCaseStatisticDto getDashboardCaseStatistic(DashboardCriteria dashboardCriteria);

	DashboardContactStatisticDto getDashboardContactStatistic(DashboardCriteria dashboardCriteria);

	Map<PathogenTestResultType, Long> getNewCasesFinalLabResultCountByResultType(DashboardCriteria dashboardCriteria);

	Map<Date, Map<CaseClassification, Integer>> getEpiCurveSeriesElementsPerCaseClassification(DashboardCriteria dashboardCriteria);

	Map<Date, Map<PresentCondition, Integer>> getEpiCurveSeriesElementsPerPresentCondition(DashboardCriteria dashboardCriteria);

	Map<Date, Map<ContactClassification, Long>> getEpiCurveSeriesElementsPerContactClassification(DashboardCriteria dashboardCriteria);

	Map<Date, Map<String, Long>> getEpiCurveSeriesElementsPerContactFollowUpStatus(DashboardCriteria dashboardCriteria);

	Map<Date, Integer> getEpiCurveSeriesElementsPerContactFollowUpUntil(DashboardCriteria dashboardCriteria);

	DashboardCaseMeasureDto getCaseMeasurePerDistrict(DashboardCriteria dashboardCriteria);

	List<DiseaseBurdenDto> getDiseaseBurden(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		Date fromDate,
		Date toDate,
		Date previousFromDate,
		Date previousToDate,
		CriteriaDateType newCaseDateType);

    Map<PathogenTestResultType, Long> getTestResultCountByResultType(DashboardCriteria dashboardCriteria);
}
