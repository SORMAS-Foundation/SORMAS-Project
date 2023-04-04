package de.symeda.sormas.backend.caze;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Year;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCountDto;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;

public class CaseStatisticsFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testQueryCaseCount() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		cazePerson.setApproximateAge(30);
		cazePerson.setApproximateAgeReferenceDate(new Date());
		cazePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
		cazePerson = getPersonFacade().save(cazePerson);
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		caze.setOutcomeDate(DateHelper.addWeeks(caze.getReportDate(), 2));
		caze = getCaseFacade().save(caze);

		StatisticsCaseCriteria criteria = new StatisticsCaseCriteria();
		int year = UtilDate.toLocalDate(caze.getSymptoms().getOnsetDate()).getYear();
		criteria.years(Arrays.asList(new Year(year), new Year(year + 1)), StatisticsCaseAttribute.ONSET_TIME);
		criteria.regions(Arrays.asList(new RegionReferenceDto(rdcf.region.getUuid(), null, null)));
		criteria.addAgeIntervals(Arrays.asList(new IntegerRange(10, 40)));

		List<StatisticsCaseCountDto> results = getCaseStatisticsFacade().queryCaseCount(criteria, null, null, null, null, false, false, null);
		// List should have one entry
		assertEquals(1, results.size());

		// try all groupings
		for (StatisticsCaseAttribute groupingAttribute : StatisticsCaseAttribute.values()) {
			StatisticsCaseSubAttribute[] subAttributes = groupingAttribute.getSubAttributes();
			if (subAttributes.length == 0) {
				getCaseStatisticsFacade().queryCaseCount(criteria, groupingAttribute, null, null, null, false, false, null);
			} else {
				for (StatisticsCaseSubAttribute subGroupingAttribute : groupingAttribute.getSubAttributes()) {
					if (subGroupingAttribute.isUsedForGrouping()) {
						getCaseStatisticsFacade().queryCaseCount(criteria, groupingAttribute, subGroupingAttribute, null, null, false, false, null);
					}
				}
			}
		}
	}

	@Test
	public void testQueryCaseCountZeroValues() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		cazePerson.setApproximateAge(30);
		cazePerson.setApproximateAgeReferenceDate(new Date());
		cazePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
		cazePerson = getPersonFacade().save(cazePerson);
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		StatisticsCaseCriteria criteria = new StatisticsCaseCriteria();
		int year = UtilDate.toLocalDate(caze.getSymptoms().getOnsetDate()).getYear();
		criteria.years(Arrays.asList(new Year(year), new Year(year + 1)), StatisticsCaseAttribute.ONSET_TIME);
		criteria.regions(Arrays.asList(new RegionReferenceDto(rdcf.region.getUuid(), null, null)));
		criteria.addAgeIntervals(Arrays.asList(new IntegerRange(10, 40)));

		List<StatisticsCaseCountDto> results =
			getCaseStatisticsFacade().queryCaseCount(criteria, StatisticsCaseAttribute.SEX, null, null, null, false, true, null);

		// List should have one entry per sex
		assertEquals(Sex.values().length, results.size());
	}

	@Test
	public void testQueryCaseCountPopulation() {

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		cazePerson.setApproximateAge(30);
		cazePerson.setApproximateAgeReferenceDate(new Date());
		cazePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
		cazePerson = getPersonFacade().save(cazePerson);
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		StatisticsCaseCriteria criteria = new StatisticsCaseCriteria();
		criteria.regions(Arrays.asList(rdcf.region));

		List<StatisticsCaseCountDto> results = getCaseStatisticsFacade()
			.queryCaseCount(criteria, StatisticsCaseAttribute.JURISDICTION, StatisticsCaseSubAttribute.REGION, null, null, true, false, null);
		assertNull(results.get(0).getPopulation());

		PopulationDataDto populationData = PopulationDataDto.build(new Date());
		RegionDto region = getRegionFacade().getByUuid(rdcf.region.getUuid());
		region.setGrowthRate(10f);
		getRegionFacade().save(region);
		populationData.setRegion(rdcf.region);
		populationData.setPopulation(new Integer(10000));
		getPopulationDataFacade().savePopulationData(Arrays.asList(populationData));

		results = getCaseStatisticsFacade().queryCaseCount(
			criteria,
			StatisticsCaseAttribute.JURISDICTION,
			StatisticsCaseSubAttribute.REGION,
			null,
			null,
			true,
			false,
			LocalDate.now().getYear() + 2);
		// List should have one entry
		assertEquals(Integer.valueOf(12214), results.get(0).getPopulation());
	}

	@Test
	public void testQueryCaseCountZeroValuesTimeIntervals() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(2023, 3, 30);

		final Date today = calendar.getTime();
		final Date oneYearAgo = DateUtils.addYears(today, -1);
		final Date fourYearsAgo = DateUtils.addYears(today, -4);

		RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		cazePerson.setApproximateAge(30);
		cazePerson.setApproximateAgeReferenceDate(new Date());
		cazePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
		cazePerson = getPersonFacade().save(cazePerson);
		CaseDataDto caze = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		CaseDataDto caze2 = creator.createCase(
			user.toReference(),
			cazePerson.toReference(),
			Disease.EVD,
			CaseClassification.PROBABLE,
			InvestigationStatus.PENDING,
			new Date(),
			rdcf);

		executeInTransaction(em -> {
			Query query = em.createQuery("select c from cases c where c.uuid=:uuid");
			query.setParameter("uuid", caze.getUuid());
			Case singleResult = (Case) query.getSingleResult();

			singleResult.setCreationDate(new Timestamp(fourYearsAgo.getTime()));
			singleResult.setReportDate(fourYearsAgo);
			em.persist(singleResult);

			Query query2 = em.createQuery("select c from cases c where c.uuid=:uuid");
			query2.setParameter("uuid", caze2.getUuid());
			Case singleResult2 = (Case) query2.getSingleResult();

			singleResult2.setCreationDate(new Timestamp(oneYearAgo.getTime()));
			singleResult2.setReportDate(oneYearAgo);
			em.persist(singleResult);
		});

		StatisticsCaseCriteria criteria = new StatisticsCaseCriteria();

		List<StatisticsCaseCountDto> resultsMonths = getCaseStatisticsFacade()
			.queryCaseCount(criteria, StatisticsCaseAttribute.REPORT_TIME, StatisticsCaseSubAttribute.MONTH_OF_YEAR, null, null, false, true, null);
		assertEquals(37, resultsMonths.size());

		List<StatisticsCaseCountDto> resultsQuarter = getCaseStatisticsFacade()
			.queryCaseCount(criteria, StatisticsCaseAttribute.REPORT_TIME, StatisticsCaseSubAttribute.QUARTER_OF_YEAR, null, null, false, true, null);
		assertEquals(13, resultsQuarter.size());

		List<StatisticsCaseCountDto> resultsEpiWeek = getCaseStatisticsFacade().queryCaseCount(
			criteria,
			StatisticsCaseAttribute.REPORT_TIME,
			StatisticsCaseSubAttribute.EPI_WEEK_OF_YEAR,
			null,
			null,
			false,
			true,
			null);
		assertEquals(157, resultsEpiWeek.size());
	}
}
