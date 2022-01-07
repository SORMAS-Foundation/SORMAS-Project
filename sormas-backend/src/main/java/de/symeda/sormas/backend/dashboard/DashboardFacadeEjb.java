package de.symeda.sormas.backend.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.time.DateUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDefinition;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardCaseStatisticDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.dashboard.DashboardFacade;
import de.symeda.sormas.api.dashboard.DashboardQuarantineDataDto;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.utils.DateHelper;
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

	public Map<Date, Map<CaseClassification, Integer>> getEpiCurveSeriesElementsPerCaseClassification(DashboardCriteria dashboardCriteria) {
		Map<Date, Map<CaseClassification, Integer>> epiCurveSeriesElements = new TreeMap<>();
		List<Date> dates = buildListOfFilteredDates(
			dashboardCriteria.getDateFrom(),
			dashboardCriteria.getDateTo(),
			dashboardCriteria.getEpiCurveGrouping(),
			dashboardCriteria.isShowMinimumEntries());
		for (int i = 0; i < dates.size(); i++) {
			dashboardCriteria = setNewCaseDatesInCaseCriteria(dates.get(i), dashboardCriteria);
			Map<CaseClassification, Integer> caseCounts = getCasesCountByClassification(dashboardCriteria);
			epiCurveSeriesElements.put(dates.get(i), caseCounts);

		}

		return epiCurveSeriesElements;
	}

	public Map<Date, Map<PresentCondition, Integer>> getEpiCurveSeriesElementsPerPresentCondition(DashboardCriteria dashboardCriteria) {
		Map<Date, Map<PresentCondition, Integer>> epiCurveSeriesElements = new TreeMap<>();
		List<Date> dates = buildListOfFilteredDates(
			dashboardCriteria.getDateFrom(),
			dashboardCriteria.getDateTo(),
			dashboardCriteria.getEpiCurveGrouping(),
			dashboardCriteria.isShowMinimumEntries());
		for (int i = 0; i < dates.size(); i++) {
			dashboardCriteria = setNewCaseDatesInCaseCriteria(dates.get(i), dashboardCriteria);

			Map<PresentCondition, Integer> caseCounts = getCasesCountPerPersonCondition(dashboardCriteria);

			epiCurveSeriesElements.put(dates.get(i), caseCounts);

		}

		return epiCurveSeriesElements;
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

	/**
	 * Builds a list that contains an object for each day, week or month between the
	 * from and to dates. Additional previous days, weeks or months might be added
	 * when showMinimumEntries is true.
	 */
	protected List<Date> buildListOfFilteredDates(Date fromDate, Date toDate, EpiCurveGrouping epiCurveGrouping, boolean showMinimumEntries) {

		List<Date> filteredDates = new ArrayList<>();
		Date fromDateDayStart = DateHelper.getStartOfDay(fromDate);
		Date toDateDayStart = DateHelper.getEndOfDay(toDate);
		Date currentDate;

		switch (epiCurveGrouping) {
		case DAY:
			if (!showMinimumEntries || DateHelper.getDaysBetween(fromDateDayStart, toDateDayStart) >= 7) {
				currentDate = fromDateDayStart;
			} else {
				currentDate = DateHelper.subtractDays(toDateDayStart, 6);
			}
			while (!currentDate.after(toDateDayStart)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addDays(currentDate, 1);
			}
			break;
		case WEEK:
			if (!showMinimumEntries || DateHelper.getWeeksBetween(fromDateDayStart, toDateDayStart) >= 7) {
				currentDate = fromDateDayStart;
			} else {
				currentDate = DateHelper.subtractWeeks(toDateDayStart, 6);
			}
			while (!currentDate.after(toDateDayStart)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addWeeks(currentDate, 1);
			}
			break;
		default:
			if (!showMinimumEntries || DateHelper.getMonthsBetween(fromDateDayStart, toDateDayStart) >= 7) {
				currentDate = fromDateDayStart;
			} else {
				currentDate = DateHelper.subtractMonths(toDateDayStart, 6);
			}
			while (!currentDate.after(toDateDayStart)) {
				filteredDates.add(currentDate);
				currentDate = DateHelper.addMonths(currentDate, 1);
			}
			break;
		}

		return filteredDates;
	}

	protected DashboardCriteria setNewCaseDatesInCaseCriteria(Date date, DashboardCriteria dashboardCriteria) {
		EpiCurveGrouping epiCurveGrouping = dashboardCriteria.getEpiCurveGrouping();
		switch (epiCurveGrouping) {
		case DAY:
			dashboardCriteria.dateBetween(DateHelper.getStartOfDay(date), DateHelper.getEndOfDay(date));
			break;
		case WEEK:
			dashboardCriteria.dateBetween(DateHelper.getStartOfWeek(date), DateHelper.getEndOfWeek(date));
			break;
		default:
			dashboardCriteria.dateBetween(DateHelper.getStartOfMonth(date), DateHelper.getEndOfMonth(date));

		}
		return dashboardCriteria;
	}

	private Predicate<DashboardQuarantineDataDto> quarantineData(Date fromDate, Date toDate) {
		return p -> {
			Date quarantineFrom = p.getQuarantineFrom();
			Date quarantineTo = p.getQuarantineTo();

			if (fromDate != null && toDate != null) {
				if (quarantineFrom != null && quarantineTo != null) {
					return quarantineTo.after(fromDate) && quarantineFrom.before(toDate);
				} else if (quarantineFrom != null) {
					return quarantineFrom.after(fromDate) && quarantineFrom.before(toDate);
				} else if (quarantineTo != null) {
					return quarantineTo.after(fromDate) && quarantineTo.before(toDate);
				}
			} else if (fromDate != null) {
				if (quarantineFrom != null) {
					return quarantineFrom.after(fromDate);
				} else if (quarantineTo != null) {
					return quarantineTo.after(fromDate);
				}
			} else if (toDate != null) {
				if (quarantineFrom != null) {
					return quarantineFrom.before(toDate);
				} else if (quarantineTo != null) {
					return quarantineTo.before(toDate);
				}
			}

			return false;
		};
	}

	public DashboardCaseStatisticDto getDashboardCaseStatistic(DashboardCriteria dashboardCriteria) {
		List<DashboardCaseDto> dashboardCases = dashboardService.getCases(dashboardCriteria);
		long newCases = dashboardCases.size();

		long fatalCasesCount = dashboardCases.stream().filter(DashboardCaseDto::wasFatal).count();
		float fatalityRate = 100 * ((float) fatalCasesCount / (float) (newCases == 0 ? 1 : newCases));
		fatalityRate = Math.round(fatalityRate * 100) / 100f;

		List<DashboardQuarantineDataDto> casesInQuarantineDtos = dashboardCases.stream()
			.map(DashboardCaseDto::getDashboardQuarantineDataDto)
			.filter(quarantineData(dashboardCriteria.getDateFrom(), dashboardCriteria.getDateTo()))
			.collect(Collectors.toList());

		long casesInQuarantineCount = casesInQuarantineDtos.size();
		long casesPlacedInQuarantineCount = casesInQuarantineDtos.stream()
			.filter(
				dashboardQuarantineDataDto -> (dashboardQuarantineDataDto.getQuarantineFrom() != null
					&& dashboardCriteria.getDateFrom().before(DateUtils.addDays(dashboardQuarantineDataDto.getQuarantineFrom(), 1))
					&& dashboardQuarantineDataDto.getQuarantineFrom().before(dashboardCriteria.getDateTo())))
			.count();

		long casesWithReferenceDefinitionFulfilledCount =
			dashboardCases.stream().filter(cases -> cases.getCaseReferenceDefinition() == CaseReferenceDefinition.FULFILLED).count();

		long outbreakDistrictCount = outbreakFacade.getOutbreakDistrictCount(
			new OutbreakCriteria().region(dashboardCriteria.getRegion())
				.district(dashboardCriteria.getDistrict())
				.disease(dashboardCriteria.getDisease())
				.reportedBetween(dashboardCriteria.getDateFrom(), dashboardCriteria.getDateTo()));

		Map<CaseClassification, Integer> casesCountByClassification =
			dashboardService.getCasesCountByClassification(dashboardCriteria.includeNotACaseClassification(true));

		return new DashboardCaseStatisticDto(
			casesCountByClassification,
			casesCountByClassification.values().stream().reduce(0, Integer::sum),
			fatalCasesCount,
			fatalityRate,
			outbreakDistrictCount,
			casesInQuarantineCount,
			casesPlacedInQuarantineCount,
			casesWithReferenceDefinitionFulfilledCount,
			dashboardService.countCasesConvertedFromContacts(dashboardCriteria),
			dashboardService.getLastReportedDistrictName(dashboardCriteria));
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
