package de.symeda.sormas.backend.caze;

import static org.junit.Assert.assertEquals;

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
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.CaseCountDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.TestDataCreator.RDCFEntities;

public class CaseStatisticsFacadeEjbTest extends AbstractBeanTest {

	@SuppressWarnings("deprecation")
	@Test
	public void testQueryCaseCount() throws Exception {
		RDCFEntities rdcf = creator.createRDCFEntities("Region", "District", "Community", "Facility");
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
		criteria.years(
				Arrays.asList(new Year(caze.getSymptoms().getOnsetDate().getYear() + 1900),
						new Year(caze.getSymptoms().getOnsetDate().getYear() + 1901)),
				StatisticsCaseAttribute.ONSET_TIME);
		criteria.regions(Arrays.asList(new RegionReferenceDto(rdcf.region.getUuid())));
		criteria.addAgeIntervals(Arrays.asList(new IntegerRange(10, 40)));
		List<CaseCountDto> results = getCaseStatisticsFacade().queryCaseCount(criteria, null, null, null, null, false, false, null);

		// List should have one entry
		assertEquals(1, results.size());
	}

	@Test
	public void testBuildCaseCountQuery() throws Exception {

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
		criteria.years(
				Arrays.asList(new Year(caze.getSymptoms().getOnsetDate().getYear() + 1900),
						new Year(caze.getSymptoms().getOnsetDate().getYear() + 1901)),
				StatisticsCaseAttribute.ONSET_TIME);
		criteria.regions(Arrays.asList(rdcf.region));
		criteria.addAgeIntervals(Arrays.asList(new IntegerRange(5, 9)));

		CaseStatisticsFacadeEjb caseStatisticsFacade = (CaseStatisticsFacadeEjb)getCaseStatisticsFacade();
		Pair<String,List<Object>> populationQuery = caseStatisticsFacade.buildPopulationQuery(criteria, StatisticsCaseAttribute.REGION_DISTRICT, StatisticsCaseSubAttribute.DISTRICT, null, null, null);

	}
}