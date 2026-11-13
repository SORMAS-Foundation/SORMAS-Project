/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.climate.ClimateCaseClassificationFilter;
import de.symeda.sormas.api.climate.ClimateCaseCriteria;
import de.symeda.sormas.api.climate.ClimateCaseFacade;
import de.symeda.sormas.api.climate.ClimateCaseSeriesDto;
import de.symeda.sormas.api.climate.ClimateCaseWeekDto;
import de.symeda.sormas.api.climate.ClimateUsualRangeMissing;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateCaseFacadeEjb.ClimateCaseFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.community.CommunityFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;

/**
 * Case-based counts beside climate: weeks by onset date else report date, classification, jurisdiction, incidence and
 * the usual range with its guardrails. The week shown is three weeks ago, and the reference cases are placed in the
 * complete epi years before it.
 */
class ClimateCaseSeriesTest extends AbstractBeanTest {

	private static final double DELTA = 1e-9;

	private final EpiWeek target = ClimateStatistics.epiWeekOf(LocalDate.now().minusDays(21));
	private final int year = ClimateStatistics.epiWeekOf(LocalDate.now()).getYear();
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private UserDto officer;
	private ClimateCaseFacade facade;

	@BeforeEach
	void setUpClimate() {
		assumeTrue(target.getYear() == year, "the week three weeks ago must lie in the current epi year");
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		// default roles other than the national and admin roles get no climate rights (decision 39)
		officer = creator.createSurveillanceOfficer(rdcf);
		executeInTransaction(em -> {
			getUserRoleService().getByUuid(officer.getUserRoles().iterator().next().getUuid()).getUserRights().add(UserRight.DASHBOARD_CLIMATE_VIEW);
		});
		user = loginWith(creator.createNationalUser());
		facade = getBean(ClimateCaseFacadeEjbLocal.class);
	}

	/**
	 * Five dengue cases in each week of the window three years ago and in the target week of the two years after: the
	 * pooled counts are 5, 5, 5, 5, 0, 0, 5, 0, 0, so p10 is 0 and p50 and p90 are 5.
	 */
	private void createReferenceCases(Disease disease, int casesPerWeek) {
		for (EpiWeek week : ClimateStatistics.weekWindow(year - 3, target.getWeek())) {
			createCases(disease, week, casesPerWeek, rdcf);
		}
		createCases(disease, new EpiWeek(year - 2, target.getWeek()), casesPerWeek, rdcf);
		createCases(disease, new EpiWeek(year - 1, target.getWeek()), casesPerWeek, rdcf);
	}

	@Test
	void testCountsByOnsetElseReportDateWithUsualRangeAndIncidence() {

		createReferenceCases(Disease.DENGUE, 5);
		createCases(Disease.DENGUE, target, 4, rdcf);
		createCase(Disease.DENGUE, target, CaseClassification.CONFIRMED, false, rdcf);
		createCase(Disease.DENGUE, target, CaseClassification.CONFIRMED, true, rdcf);
		createCase(Disease.DENGUE, target, CaseClassification.NO_CASE, true, rdcf);
		CaseDataDto deleted = createCase(Disease.DENGUE, target, CaseClassification.SUSPECT, true, rdcf);
		executeInTransaction(em -> {
			getCaseService().getByUuid(deleted.getUuid()).setDeleted(true);
		});
		creator.createPopulationData(rdcf.region, rdcf.district, 200_000, new Date());

		ClimateCaseSeriesDto series = facade.getCaseSeries(criteria().district(rdcf.district)).get(0);

		assertEquals(Disease.DENGUE, series.getDisease());
		assertEquals(Arrays.asList(year - 3, year - 2, year - 1), series.getYearsWithCases(), "no cases five and four years ago");
		ClimateCaseWeekDto week = series.getWeeks().get(0);
		assertEquals(6, week.getCount(), "not a case and the deleted case are left out");
		assertEquals(1, week.getReportDateCount());
		assertEquals(0.0, week.getUsualRange().getP10(), DELTA);
		assertEquals(5.0, week.getUsualRange().getP50(), DELTA);
		assertEquals(5.0, week.getUsualRange().getP90(), DELTA);
		assertTrue(week.isAboveUsualRange());
		assertTrue(series.isIncidenceAvailable());
		assertEquals(3.0, week.getIncidence(), DELTA);
		assertEquals(2.5, week.getUsualRangeIncidence().getP90(), DELTA);
		assertEquals(1, series.countWeeksAboveUsualRange());
		assertEquals(1, series.getRecentWeeks());
		assertEquals(1, series.getRecentWeeksAbove());

		ClimateCaseWeekDto confirmed = facade.getCaseSeries(
			criteria().district(rdcf.district).classification(ClimateCaseClassificationFilter.CONFIRMED)).get(0).getWeeks().get(0);
		assertEquals(2, confirmed.getCount());
	}

	@Test
	void testConfirmedCasesByDefault() {
		assertEquals(ClimateCaseClassificationFilter.CONFIRMED, new ClimateCaseCriteria().getClassification(), "decision 145");
	}

	@Test
	void testUsualRangeGuardrails() {

		createReferenceCases(Disease.DENGUE, 5);
		createReferenceCases(Disease.CHOLERA, 1);

		List<ClimateCaseSeriesDto> series =
			facade.getCaseSeries(criteria().diseases(Arrays.asList(Disease.CHOLERA, Disease.DENGUE)).district(rdcf.district));
		assertEquals(Disease.CHOLERA, series.get(0).getDisease());
		ClimateCaseWeekDto cholera = series.get(0).getWeeks().get(0);
		assertNull(cholera.getUsualRange());
		assertEquals(ClimateUsualRangeMissing.TOO_FEW_CASES, cholera.getUsualRangeMissing(), "median of 1 and 0s is below 5");
		assertEquals(0, cholera.getCount(), "a week without cases is 0");
		assertFalse(series.get(0).isIncidenceAvailable());
		assertNull(cholera.getIncidence());

		DiseaseConfigurationDto dengue = getDiseaseConfigurationFacade().getDiseaseConfiguration(Disease.DENGUE);
		dengue.setUsualRangeExcludedYears(Collections.singleton(year - 1));
		getDiseaseConfigurationFacade().saveDiseaseConfiguration(dengue);
		getDiseaseConfigurationFacade().loadData();

		ClimateCaseSeriesDto withExclusion = facade.getCaseSeries(criteria().district(rdcf.district)).get(0);
		assertEquals(Set.of(year - 1), withExclusion.getExcludedYears());
		assertEquals(Arrays.asList(year - 3, year - 2), withExclusion.getYearsWithCases());
		assertEquals(ClimateUsualRangeMissing.NOT_ENOUGH_HISTORY, withExclusion.getWeeks().get(0).getUsualRangeMissing());
	}

	@Test
	void testRegionSumsItsDistrictsWithinTheUsersJurisdiction() {

		Region region = getRegionService().getByUuid(rdcf.region.getUuid());
		District second = creator.createDistrict("Second", region);
		Community secondCommunity = creator.createCommunity("Second community", second);
		Facility secondFacility = creator.createFacility("Second facility", region, second, secondCommunity);
		TestDataCreator.RDCF secondRdcf = new TestDataCreator.RDCF(
			rdcf.region,
			DistrictFacadeEjb.toReferenceDto(second),
			CommunityFacadeEjb.toReferenceDto(secondCommunity),
			new FacilityReferenceDto(secondFacility.getUuid(), secondFacility.getName(), null));
		createCases(Disease.DENGUE, target, 3, rdcf);
		createCases(Disease.DENGUE, target, 2, secondRdcf);
		creator.createPopulationData(rdcf.region, rdcf.district, 100_000, new Date());

		ClimateCaseSeriesDto series = facade.getCaseSeries(criteria().includeDistricts(true)).get(0);
		assertNull(series.getDistrict());
		assertEquals(5, series.getWeeks().get(0).getCount());
		assertFalse(series.isIncidenceAvailable(), "the second district has no population");
		assertEquals(Collections.singletonList(DistrictFacadeEjb.toReferenceDto(second)), series.getDistrictsWithoutPopulation());
		assertEquals(2, series.getDistrictSeries().size());
		assertEquals(3.0, series.getDistrictSeries().get(0).getWeeks().get(0).getIncidence(), DELTA);
		assertEquals(2, series.getDistrictSeries().get(1).getWeeks().get(0).getCount());
		assertEquals(ClimateUsualRangeMissing.NOT_ENOUGH_HISTORY, series.getWeeks().get(0).getUsualRangeMissing());

		loginWith(officer);
		assertEquals(3, facade.getCaseSeries(criteria()).get(0).getWeeks().get(0).getCount(), "an officer sees only their district's cases");
	}

	private ClimateCaseCriteria criteria() {
		// these tests count every case but not a case, and check the confirmed filter on its own
		return new ClimateCaseCriteria().region(rdcf.region)
			.weeks(target, target)
			.diseases(Collections.singletonList(Disease.DENGUE))
			.classification(ClimateCaseClassificationFilter.ALL_BUT_NOT_A_CASE);
	}

	private void createCases(Disease disease, EpiWeek week, int count, TestDataCreator.RDCF place) {
		for (int i = 0; i < count; i++) {
			createCase(disease, week, CaseClassification.SUSPECT, true, place);
		}
	}

	/**
	 * Creates a case reported on the Wednesday of the week, with onset that day when {@code withOnset} is set.
	 */
	private CaseDataDto createCase(Disease disease, EpiWeek week, CaseClassification classification, boolean withOnset, TestDataCreator.RDCF place) {
		Date day = ClimateCalendar.toDate(ClimateStatistics.firstDay(week).plusDays(2));
		return creator.createCase(user.toReference(), place, c -> {
			c.setDisease(disease);
			c.setCaseClassification(classification);
			c.setReportDate(day);
			c.getSymptoms().setOnsetDate(withOnset ? day : null);
		});
	}
}
