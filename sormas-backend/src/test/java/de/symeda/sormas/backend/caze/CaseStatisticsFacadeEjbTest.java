package de.symeda.sormas.backend.caze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.IntegerRange;
import de.symeda.sormas.api.Year;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCountDto;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.util.DateHelper8;

public class CaseStatisticsFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testQueryCaseCount() throws Exception {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		cazePerson.setApproximateAge(30);
		cazePerson.setApproximateAgeReferenceDate(new Date());
		cazePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
		cazePerson = getPersonFacade().savePerson(cazePerson);
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		StatisticsCaseCriteria criteria = new StatisticsCaseCriteria();
		int year = DateHelper8.toLocalDate(caze.getSymptoms().getOnsetDate()).getYear();
		criteria.years(Arrays.asList(new Year(year), new Year(year+1)), StatisticsCaseAttribute.ONSET_TIME);
		criteria.regions(Arrays.asList(new RegionReferenceDto(rdcf.region.getUuid())));
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
	public void testQueryCaseCountZeroValues() throws Exception {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		cazePerson.setApproximateAge(30);
		cazePerson.setApproximateAgeReferenceDate(new Date());
		cazePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
		cazePerson = getPersonFacade().savePerson(cazePerson);
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		StatisticsCaseCriteria criteria = new StatisticsCaseCriteria();
		int year = DateHelper8.toLocalDate(caze.getSymptoms().getOnsetDate()).getYear();
		criteria.years(Arrays.asList(new Year(year), new Year(year+1)), StatisticsCaseAttribute.ONSET_TIME);
		criteria.regions(Arrays.asList(new RegionReferenceDto(rdcf.region.getUuid())));
		criteria.addAgeIntervals(Arrays.asList(new IntegerRange(10, 40)));
		
		List<StatisticsCaseCountDto> results = getCaseStatisticsFacade().queryCaseCount(criteria, StatisticsCaseAttribute.SEX, null, null, null, false, true, null);

		// List should have one entry per sex and also unknown
		assertEquals(Sex.values().length + 1, results.size());
	}

	@Test
	public void testQueryCaseCountPopulation() throws Exception {
		RDCF rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Surv", "Sup", UserRole.SURVEILLANCE_SUPERVISOR);
		PersonDto cazePerson = creator.createPerson("Case", "Person");
		cazePerson.setApproximateAge(30);
		cazePerson.setApproximateAgeReferenceDate(new Date());
		cazePerson.setApproximateAgeType(ApproximateAgeType.YEARS);
		cazePerson = getPersonFacade().savePerson(cazePerson);
		CaseDataDto caze = creator.createCase(user.toReference(), cazePerson.toReference(), Disease.EVD,
				CaseClassification.PROBABLE, InvestigationStatus.PENDING, new Date(), rdcf);
		caze = getCaseFacade().getCaseDataByUuid(caze.getUuid());

		StatisticsCaseCriteria criteria = new StatisticsCaseCriteria();
		criteria.regions(Arrays.asList(rdcf.region));

		List<StatisticsCaseCountDto> results = getCaseStatisticsFacade().queryCaseCount(criteria, StatisticsCaseAttribute.JURISDICTION, StatisticsCaseSubAttribute.REGION, null, null, true, false, null);
		assertNull(results.get(0).getPopulation());
		
		PopulationDataDto populationData = PopulationDataDto.build(new Date());
		RegionDto region = getRegionFacade().getRegionByUuid(rdcf.region.getUuid());
		region.setGrowthRate(10f);
		getRegionFacade().saveRegion(region);
		populationData.setRegion(rdcf.region);
		populationData.setPopulation(new Integer(10000));
		getPopulationDataFacade().savePopulationData(Arrays.asList(populationData));

		results = getCaseStatisticsFacade().queryCaseCount(criteria, StatisticsCaseAttribute.JURISDICTION, StatisticsCaseSubAttribute.REGION, null, null, true, false, LocalDate.now().getYear() + 2);
		// List should have one entry
		assertEquals(Integer.valueOf(12214), results.get(0).getPopulation());

	}
}