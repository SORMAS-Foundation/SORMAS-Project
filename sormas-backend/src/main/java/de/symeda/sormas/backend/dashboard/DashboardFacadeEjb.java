package de.symeda.sormas.backend.dashboard;

import static de.symeda.sormas.api.dashboard.DashboardContactStatisticDto.CURRENT_CONTACTS;
import static de.symeda.sormas.api.dashboard.DashboardContactStatisticDto.PREVIOUS_CONTACTS;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.time.DateUtils;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseReferenceDefinition;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.dashboard.DashboardCaseMeasureDto;
import de.symeda.sormas.api.dashboard.DashboardCaseStatisticDto;
import de.symeda.sormas.api.dashboard.DashboardContactDto;
import de.symeda.sormas.api.dashboard.DashboardContactFollowUpDto;
import de.symeda.sormas.api.dashboard.DashboardContactStatisticDto;
import de.symeda.sormas.api.dashboard.DashboardContactStoppedFollowUpDto;
import de.symeda.sormas.api.dashboard.DashboardContactVisitDto;
import de.symeda.sormas.api.dashboard.DashboardCriteria;
import de.symeda.sormas.api.dashboard.DashboardEventDto;
import de.symeda.sormas.api.dashboard.DashboardFacade;
import de.symeda.sormas.api.dashboard.DashboardQuarantineDataDto;
import de.symeda.sormas.api.dashboard.EpiCurveGrouping;
import de.symeda.sormas.api.disease.DiseaseBurdenDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.criteria.CriteriaDateType;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb;
import de.symeda.sormas.backend.event.EventFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.outbreak.OutbreakFacadeEjb;
import de.symeda.sormas.backend.sample.SampleFacadeEjb;
import de.symeda.sormas.backend.util.RightsAllowed;

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
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;

	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;

	@EJB
	private DashboardService dashboardService;

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
	public List<DashboardCaseDto> getCases(DashboardCriteria dashboardCriteria) {
		return dashboardService.getCases(dashboardCriteria);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
	public Map<CaseClassification, Integer> getCasesCountByClassification(DashboardCriteria dashboardCriteria) {
		return dashboardService.getCasesCountByClassification(dashboardCriteria);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
	public String getLastReportedDistrictName(DashboardCriteria dashboardCriteria) {
		return dashboardService.getLastReportedDistrictName(dashboardCriteria);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
	public Map<PathogenTestResultType, Long> getNewCasesFinalLabResultCountByResultType(DashboardCriteria dashboardCriteria) {
		return dashboardService.getNewTestResultCountByResultType(dashboardCriteria);
	}

	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
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

	@RightsAllowed(UserRight._DASHBOARD_SURVEILLANCE_VIEW)
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

	@RightsAllowed(UserRight._DASHBOARD_SURVEILLANCE_VIEW)
	public Map<Date, Map<ContactClassification, Long>> getEpiCurveSeriesElementsPerContactClassification(DashboardCriteria dashboardCriteria) {
		Map<Date, Map<ContactClassification, Long>> epiCurveSeriesElements = new TreeMap<>();
		List<Date> criteriaIntervalStartDates = buildListOfFilteredDates(
			dashboardCriteria.getDateFrom(),
			dashboardCriteria.getDateTo(),
			dashboardCriteria.getEpiCurveGrouping(),
			dashboardCriteria.isShowMinimumEntries());

		ContactCriteria contactCriteria = new ContactCriteria().disease(dashboardCriteria.getDisease())
			.region(dashboardCriteria.getRegion())
			.district(dashboardCriteria.getDistrict());

		criteriaIntervalStartDates.forEach(intervalStartDate -> {
			contactCriteria.reportDateBetween(intervalStartDate, getIntervalEndDate(intervalStartDate, dashboardCriteria.getEpiCurveGrouping()));
			Map<ContactClassification, Long> contactClassifications = contactFacade.getNewContactCountPerClassification(contactCriteria);
			epiCurveSeriesElements.put(intervalStartDate, contactClassifications);
		});
		return epiCurveSeriesElements;

	}

	@RightsAllowed(UserRight._DASHBOARD_SURVEILLANCE_VIEW)
	public Map<Date, Map<String, Long>> getEpiCurveSeriesElementsPerContactFollowUpStatus(DashboardCriteria dashboardCriteria) {
		Map<Date, Map<String, Long>> epiCurveSeriesElements = new TreeMap<>();
		List<Date> criteriaIntervalStartDates = buildListOfFilteredDates(
			dashboardCriteria.getDateFrom(),
			dashboardCriteria.getDateTo(),
			dashboardCriteria.getEpiCurveGrouping(),
			dashboardCriteria.isShowMinimumEntries());

		ContactCriteria contactCriteria = new ContactCriteria().disease(dashboardCriteria.getDisease())
			.region(dashboardCriteria.getRegion())
			.district(dashboardCriteria.getDistrict());

		criteriaIntervalStartDates.forEach(intervalStartDate -> {
			contactCriteria.reportDateBetween(intervalStartDate, getIntervalEndDate(intervalStartDate, dashboardCriteria.getEpiCurveGrouping()));
			Map<FollowUpStatus, Long> contactCounts = contactFacade.getNewContactCountPerFollowUpStatus(contactCriteria);
			Map<String, Long> followUpClassificationMap =
				contactCounts.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toShortString(), e -> e.getValue()));
			Map<ContactStatus, Long> contactStatusCounts = contactFacade.getNewContactCountPerStatus(contactCriteria);
			followUpClassificationMap.put(ContactStatus.CONVERTED.toString(), contactStatusCounts.get(ContactStatus.CONVERTED));
			epiCurveSeriesElements.put(intervalStartDate, followUpClassificationMap);
		});
		return epiCurveSeriesElements;

	}

	@RightsAllowed(UserRight._DASHBOARD_SURVEILLANCE_VIEW)
	public Map<Date, Integer> getEpiCurveSeriesElementsPerContactFollowUpUntil(DashboardCriteria dashboardCriteria) {
		Map<Date, Integer> epiCurveSeriesElements = new TreeMap<>();
		List<Date> criteriaIntervalStartDates = buildListOfFilteredDates(
			dashboardCriteria.getDateFrom(),
			dashboardCriteria.getDateTo(),
			dashboardCriteria.getEpiCurveGrouping(),
			dashboardCriteria.isShowMinimumEntries());

		ContactCriteria contactCriteria = new ContactCriteria().disease(dashboardCriteria.getDisease())
			.region(dashboardCriteria.getRegion())
			.district(dashboardCriteria.getDistrict());

		criteriaIntervalStartDates.forEach(intervalStartDate -> {
			contactCriteria.reportDateBetween(intervalStartDate, getIntervalEndDate(intervalStartDate, dashboardCriteria.getEpiCurveGrouping()));
			epiCurveSeriesElements.put(intervalStartDate, contactFacade.getFollowUpUntilCount(contactCriteria));
		});
		return epiCurveSeriesElements;
	}

	@RightsAllowed(UserRight._DASHBOARD_SURVEILLANCE_VIEW)
	public DashboardCaseMeasureDto getCaseMeasurePerDistrict(DashboardCriteria dashboardCriteria) {
		Map<DistrictDto, BigDecimal> caseMeasurePerDistrictMap = new LinkedHashMap<>();
		BigDecimal districtValuesLowerQuartile;
		BigDecimal districtValuesMedianQuartile;
		BigDecimal districtValuesUpperQuartile;

		List<DataHelper.Pair<DistrictDto, BigDecimal>> caseMeasurePerDistrict = caseFacade.getCaseMeasurePerDistrict(
			dashboardCriteria.getDateFrom(),
			dashboardCriteria.getDateTo(),
			dashboardCriteria.getDisease(),
			dashboardCriteria.getCaseMeasure());

		if (dashboardCriteria.getCaseMeasure() == CaseMeasure.CASE_COUNT) {
			caseMeasurePerDistrictMap =
				caseMeasurePerDistrict.stream().collect(Collectors.toMap(DataHelper.Pair::getElement0, DataHelper.Pair::getElement1));

			districtValuesLowerQuartile =
				caseMeasurePerDistrict.size() > 0 ? caseMeasurePerDistrict.get((int) (caseMeasurePerDistrict.size() * 0.25)).getElement1() : null;
			districtValuesMedianQuartile =
				caseMeasurePerDistrict.size() > 0 ? caseMeasurePerDistrict.get((int) (caseMeasurePerDistrict.size() * 0.5)).getElement1() : null;
			districtValuesUpperQuartile =
				caseMeasurePerDistrict.size() > 0 ? caseMeasurePerDistrict.get((int) (caseMeasurePerDistrict.size() * 0.75)).getElement1() : null;

		} else {
			// For case incidence, districts without or with a population <= 0 should not be
			// used for the calculation of the quartiles because they will falsify the
			// result
			List<DataHelper.Pair<DistrictDto, BigDecimal>> measurePerDistrictWithoutMissingPopulations = new ArrayList<>();
			measurePerDistrictWithoutMissingPopulations.addAll(caseMeasurePerDistrict);
			measurePerDistrictWithoutMissingPopulations.removeIf(d -> d.getElement1() == null || d.getElement1().intValue() <= 0);

			caseMeasurePerDistrictMap = measurePerDistrictWithoutMissingPopulations.stream()
				.collect(Collectors.toMap(DataHelper.Pair::getElement0, DataHelper.Pair::getElement1));

			districtValuesLowerQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0
				? measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.25)).getElement1()
				: null;
			districtValuesMedianQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0
				? measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.5)).getElement1()
				: null;
			districtValuesUpperQuartile = measurePerDistrictWithoutMissingPopulations.size() > 0
				? measurePerDistrictWithoutMissingPopulations.get((int) (measurePerDistrictWithoutMissingPopulations.size() * 0.75)).getElement1()
				: null;
		}

		return new DashboardCaseMeasureDto(
			caseMeasurePerDistrictMap,
			districtValuesLowerQuartile,
			districtValuesMedianQuartile,
			districtValuesUpperQuartile);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
	public long countCasesConvertedFromContacts(DashboardCriteria dashboardCriteria) {
		return dashboardService.countCasesConvertedFromContacts(dashboardCriteria);
	}

	@Override
	@RightsAllowed(UserRight._DASHBOARD_SURVEILLANCE_VIEW)
	public Map<PresentCondition, Integer> getCasesCountPerPersonCondition(DashboardCriteria dashboardCriteria) {
		return dashboardService.getCasesCountPerPersonCondition(dashboardCriteria);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
	public List<DashboardEventDto> getNewEvents(DashboardCriteria dashboardCriteria) {
		return dashboardService.getNewEvents(dashboardCriteria);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
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

	protected Date getIntervalEndDate(Date intervalStartDate, EpiCurveGrouping epiCurveGrouping) {
		switch (epiCurveGrouping) {
		case DAY:
			return DateHelper.getEndOfDay(intervalStartDate);
		case WEEK:
			return DateHelper.getEndOfWeek(intervalStartDate);
		default:
			return DateHelper.getEndOfMonth(intervalStartDate);
		}
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

	@RightsAllowed(UserRight._DASHBOARD_SURVEILLANCE_VIEW)
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

	@RightsAllowed(UserRight._DASHBOARD_CONTACT_VIEW)
	public DashboardContactStatisticDto getDashboardContactStatistic(DashboardCriteria dashboardCriteria) {

		List<DashboardContactDto> dashboardContacts = contactFacade.getContactsForDashboard(
			dashboardCriteria.getRegion(),
			dashboardCriteria.getDistrict(),
			dashboardCriteria.getDisease(),
			dashboardCriteria.getDateFrom(),
			dashboardCriteria.getDateTo());

		List<DashboardContactDto> previousDashboardContacts = contactFacade.getContactsForDashboard(
			dashboardCriteria.getRegion(),
			dashboardCriteria.getDistrict(),
			dashboardCriteria.getDisease(),
			dashboardCriteria.getPreviousDateFrom(),
			dashboardCriteria.getPreviousDateTo());

		int contactsCount = dashboardContacts.size();

		int newContactsCount = (int) dashboardContacts.stream()
			.filter(c -> c.getReportDate().after(dashboardCriteria.getDateFrom()) || c.getReportDate().equals(dashboardCriteria.getDateFrom()))
			.count();
		int newContactsPercentage = calculatePercentage(newContactsCount, contactsCount);
		int symptomaticContactsCount = (int) dashboardContacts.stream().filter(c -> Boolean.TRUE.equals(c.getSymptomatic())).count();
		int symptomaticContactsPercentage = calculatePercentage(symptomaticContactsCount, contactsCount);
		int contactClassificationUnconfirmedCount =
			(int) dashboardContacts.stream().filter(c -> c.getContactClassification() == ContactClassification.UNCONFIRMED).count();
		int contactClassificationUnconfirmedPercentage = calculatePercentage(contactClassificationUnconfirmedCount, contactsCount);
		int contactClassificationConfirmedCount =
			(int) dashboardContacts.stream().filter(c -> c.getContactClassification() == ContactClassification.CONFIRMED).count();
		int contactClassificationConfirmedPercentage = calculatePercentage(contactClassificationConfirmedCount, contactsCount);
		int contactClassificationNotAContactCount =
			(int) dashboardContacts.stream().filter(c -> c.getContactClassification() == ContactClassification.NO_CONTACT).count();
		int contactClassificationNotAContactPercentage = calculatePercentage(contactClassificationNotAContactCount, contactsCount);
		Map<Disease, Map<String, Integer>> diseaseMap = new TreeMap<>();
		for (Disease disease : diseaseConfigurationFacade.getAllDiseasesWithFollowUp()) {
			Map<String, Integer> countValues = new HashMap<>();
			countValues.put(PREVIOUS_CONTACTS, (int) previousDashboardContacts.stream().filter(c -> c.getDisease() == disease).count());
			countValues.put(CURRENT_CONTACTS, (int) dashboardContacts.stream().filter(c -> c.getDisease() == disease).count());
			diseaseMap.put(disease, countValues);
		}

		Map<Disease, Map<String, Integer>> orderedDiseaseMap = diseaseMap.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByValue((o1, o2) -> -Integer.compare(o1.get(CURRENT_CONTACTS), o2.get(CURRENT_CONTACTS))))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

		DashboardContactFollowUpDto dashboardContactFollowUp = calculateContactFollowUpStatistics(dashboardContacts, dashboardCriteria.getDateTo());
		DashboardContactStoppedFollowUpDto dashboardContactStoppedFollowUp = calculateContactStoppedFollowUpStatistics(dashboardContacts);
		DashboardContactVisitDto dashboardContactVisit = calculateContactVisitStatistics(dashboardContacts, previousDashboardContacts);

		return new DashboardContactStatisticDto(
			contactsCount,
			newContactsCount,
			newContactsPercentage,
			symptomaticContactsCount,
			symptomaticContactsPercentage,
			contactClassificationConfirmedCount,
			contactClassificationConfirmedPercentage,
			contactClassificationUnconfirmedCount,
			contactClassificationUnconfirmedPercentage,
			contactClassificationNotAContactCount,
			contactClassificationNotAContactPercentage,
			orderedDiseaseMap,
			dashboardContactFollowUp,
			dashboardContactStoppedFollowUp,
			dashboardContactVisit);
	}

	private DashboardContactFollowUpDto calculateContactFollowUpStatistics(List<DashboardContactDto> contacts, Date dateTo) {

		List<DashboardContactDto> followUpContacts =
			contacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.FOLLOW_UP).collect(Collectors.toList());

		int followUpContactsCount = followUpContacts.size();

		int cooperativeContactsCount = (int) followUpContacts.stream().filter(c -> c.getLastVisitStatus() == VisitStatus.COOPERATIVE).count();
		int uncooperativeContactsCount = (int) followUpContacts.stream().filter(c -> c.getLastVisitStatus() == VisitStatus.UNCOOPERATIVE).count();
		int unavailableContactsCount = (int) followUpContacts.stream().filter(c -> c.getLastVisitStatus() == VisitStatus.UNAVAILABLE).count();
		int notVisitedContactsCount = (int) followUpContacts.stream().filter(c -> c.getLastVisitStatus() == null).count();
		int cooperativeContactsPercentage = calculatePercentage(cooperativeContactsCount, followUpContactsCount);
		int uncooperativeContactsPercentage = calculatePercentage(uncooperativeContactsCount, followUpContactsCount);
		int unavailableContactsPercentage = calculatePercentage(unavailableContactsCount, followUpContactsCount);
		int notVisitedContactsPercentage = calculatePercentage(notVisitedContactsCount, followUpContactsCount);

		int missedVisitsOneDayCount = 0;
		int missedVisitsTwoDaysCount = 0;
		int missedVisitsThreeDaysCount = 0;
		int missedVisitsGtThreeDaysCount = 0;

		for (DashboardContactDto contact : followUpContacts) {
			Date lastVisitDateTime = contact.getLastVisitDateTime() != null ? contact.getLastVisitDateTime() : contact.getReportDate();

			Date referenceDate = dateTo.after(new Date()) ? new Date() : dateTo;

			int missedDays = DateHelper.getFullDaysBetween(lastVisitDateTime, referenceDate);

			switch (missedDays <= 3 ? missedDays : 4) {
			case 1:
				missedVisitsOneDayCount++;
				break;
			case 2:
				missedVisitsTwoDaysCount++;
				break;
			case 3:
				missedVisitsThreeDaysCount++;
				break;
			case 4:
				missedVisitsGtThreeDaysCount++;
				break;
			default:
				break;
			}

		}

		return new DashboardContactFollowUpDto(
			followUpContactsCount,
			cooperativeContactsCount,
			cooperativeContactsPercentage,
			uncooperativeContactsCount,
			uncooperativeContactsPercentage,
			unavailableContactsCount,
			unavailableContactsPercentage,
			notVisitedContactsCount,
			notVisitedContactsPercentage,
			missedVisitsOneDayCount,
			missedVisitsTwoDaysCount,
			missedVisitsThreeDaysCount,
			missedVisitsGtThreeDaysCount);
	}

	private DashboardContactStoppedFollowUpDto calculateContactStoppedFollowUpStatistics(List<DashboardContactDto> contacts) {

		List<DashboardContactDto> stoppedFollowUpContacts = contacts.stream()
			.filter(c -> c.getFollowUpStatus() != FollowUpStatus.NO_FOLLOW_UP && c.getFollowUpStatus() != FollowUpStatus.FOLLOW_UP)
			.collect(Collectors.toList());

		int stoppedFollowUpContactsCount = stoppedFollowUpContacts.size();

		int followUpCompletedCount = (int) stoppedFollowUpContacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.COMPLETED).count();
		int followUpCanceledCount = (int) stoppedFollowUpContacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.CANCELED).count();
		int lostToFollowUpCount = (int) stoppedFollowUpContacts.stream().filter(c -> c.getFollowUpStatus() == FollowUpStatus.LOST).count();
		int contactStatusConvertedCount = (int) stoppedFollowUpContacts.stream().filter(c -> c.getContactStatus() == ContactStatus.CONVERTED).count();

		int followUpCompletedPercentage = calculatePercentage(followUpCompletedCount, stoppedFollowUpContactsCount);
		int followUpCanceledPercentage = calculatePercentage(followUpCanceledCount, stoppedFollowUpContactsCount);
		int lostToFollowUpPercentage = calculatePercentage(lostToFollowUpCount, stoppedFollowUpContactsCount);
		int contactStatusConvertedPercentage = calculatePercentage(contactStatusConvertedCount, stoppedFollowUpContactsCount);

		return new DashboardContactStoppedFollowUpDto(
			stoppedFollowUpContactsCount,
			followUpCompletedCount,
			followUpCompletedPercentage,
			followUpCanceledCount,
			followUpCanceledPercentage,
			lostToFollowUpCount,
			lostToFollowUpPercentage,
			contactStatusConvertedCount,
			contactStatusConvertedPercentage);
	}

	private DashboardContactVisitDto calculateContactVisitStatistics(List<DashboardContactDto> contacts, List<DashboardContactDto> previousContacts) {
		Map<VisitStatus, Integer> visitStatusMap = new EnumMap<>(VisitStatus.class);
		Map<VisitStatus, Integer> previousVisitStatusMap = new EnumMap<>(VisitStatus.class);
		int doneEssentialVisitsCount = 0;	// only visits that needed to be done, i.e. at most the amount of follow-up days
		int previousDoneEssentialVisitsCount = 0;

		Date now = new Date();
		int totalFollowUpDays = 0;
		int previousTotalFollowUpDays = 0;
		for (DashboardContactDto contact : contacts) {
			for (VisitStatus visitStatus : contact.getVisitStatusMap().keySet()) {
				int value = 0;
				if (visitStatusMap.containsKey(visitStatus)) {
					value = visitStatusMap.get(visitStatus);
				}
				visitStatusMap.put(visitStatus, value + contact.getVisitStatusMap().get(visitStatus));
			}
			if (contact.getFollowUpUntil() != null) {
				int contactFollowUpDays = Math.min(
					DateHelper.getDaysBetween(contact.getReportDate(), now),
					DateHelper.getDaysBetween(contact.getReportDate(), contact.getFollowUpUntil()));
				totalFollowUpDays += contactFollowUpDays;
				int visitCount = contact.getVisitStatusMap().values().stream().reduce(0, Integer::sum);
				doneEssentialVisitsCount += (Math.min(visitCount, contactFollowUpDays));
			}
		}

		for (DashboardContactDto contact : previousContacts) {
			for (VisitStatus visitStatus : contact.getVisitStatusMap().keySet()) {
				int value = 0;
				if (previousVisitStatusMap.containsKey(visitStatus)) {
					value = previousVisitStatusMap.get(visitStatus);
				}
				previousVisitStatusMap.put(visitStatus, value + contact.getVisitStatusMap().get(visitStatus));
			}
			if (contact.getFollowUpUntil() != null) {
				int contactFollowUpDays = Math.min(
					DateHelper.getDaysBetween(contact.getReportDate(), now),
					DateHelper.getDaysBetween(contact.getReportDate(), contact.getFollowUpUntil()));
				previousTotalFollowUpDays += contactFollowUpDays;
				int visitCount = contact.getVisitStatusMap().values().stream().reduce(0, Integer::sum);
				previousDoneEssentialVisitsCount += (Math.min(visitCount, contactFollowUpDays));
			}
		}

		int visitsCount = visitStatusMap.values().stream().reduce(0, Integer::sum);

		int missedVisitsCount = totalFollowUpDays - doneEssentialVisitsCount;
		int unavailableVisitsCount = Optional.ofNullable(visitStatusMap.get(VisitStatus.UNAVAILABLE)).orElse(0).intValue();
		int uncooperativeVisitsCount = Optional.ofNullable(visitStatusMap.get(VisitStatus.UNCOOPERATIVE)).orElse(0).intValue();
		int cooperativeVisitsCount = Optional.ofNullable(visitStatusMap.get(VisitStatus.COOPERATIVE)).orElse(0).intValue();
		int previousMissedVisitsCount = previousTotalFollowUpDays - previousDoneEssentialVisitsCount;
		int previousUnavailableVisitsCount = Optional.ofNullable(previousVisitStatusMap.get(VisitStatus.UNAVAILABLE)).orElse(0).intValue();
		int previousUncooperativeVisitsCount = Optional.ofNullable(previousVisitStatusMap.get(VisitStatus.UNCOOPERATIVE)).orElse(0).intValue();
		int previousCooperativeVisitsCount = Optional.ofNullable(previousVisitStatusMap.get(VisitStatus.COOPERATIVE)).orElse(0).intValue();

		int missedVisitsGrowth = calculateGrowth(missedVisitsCount, previousMissedVisitsCount);
		int unavailableVisitsGrowth = calculateGrowth(unavailableVisitsCount, previousUnavailableVisitsCount);
		int uncooperativeVisitsGrowth = calculateGrowth(uncooperativeVisitsCount, previousUncooperativeVisitsCount);
		int cooperativeVisitsGrowth = calculateGrowth(cooperativeVisitsCount, previousCooperativeVisitsCount);

		return new DashboardContactVisitDto(
			visitsCount,
			missedVisitsCount,
			missedVisitsGrowth,
			unavailableVisitsCount,
			unavailableVisitsGrowth,
			uncooperativeVisitsCount,
			uncooperativeVisitsGrowth,
			cooperativeVisitsCount,
			cooperativeVisitsGrowth,
			previousMissedVisitsCount,
			previousUnavailableVisitsCount,
			previousUncooperativeVisitsCount,
			previousCooperativeVisitsCount);

	}

	private int calculateGrowth(int currentCount, int previousCount) {
		return currentCount == 0
			? (previousCount > 0 ? -100 : 0)
			: previousCount == 0
				? (currentCount > 0 ? Integer.MIN_VALUE : 0)
				: Math.round(((currentCount - previousCount * 1.0f) / previousCount) * 100.0f);
	}

	private int calculatePercentage(int amount, int totalAmount) {
		return totalAmount == 0 ? 0 : (int) ((amount * 100.0f) / totalAmount);
	}

	@Override
	@RightsAllowed({
		UserRight._DASHBOARD_SURVEILLANCE_VIEW,
		UserRight._DASHBOARD_CONTACT_VIEW })
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

	@Override
	@RightsAllowed({
			UserRight._DASHBOARD_SURVEILLANCE_VIEW,
			UserRight._DASHBOARD_CONTACT_VIEW })
	public Map<PathogenTestResultType, Long> getTestResultCountByResultType(DashboardCriteria dashboardCriteria) {
		return dashboardService.getNewTestResultCountByResultType(dashboardCriteria);
	}

	@LocalBean
	@Stateless
	public static class DashboardFacadeEjbLocal extends DashboardFacadeEjb {

	}
}
