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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.climate.ClimateCaseClassificationFilter;
import de.symeda.sormas.api.climate.ClimateCaseCountSource;
import de.symeda.sormas.api.climate.ClimateCaseCriteria;
import de.symeda.sormas.api.climate.ClimateCaseFacade;
import de.symeda.sormas.api.climate.ClimateCaseSeriesDto;
import de.symeda.sormas.api.climate.ClimateCaseWeekDto;
import de.symeda.sormas.api.climate.ClimateUsualRangeMissing;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateCaseFacadeEjb.ClimateCaseFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.report.AggregateReport;

/**
 * The Climate dashboard's counts for diseases without case surveillance: their lab-confirmed aggregate reports (decision
 * 147).
 */
class ClimateAggregateSeriesTest extends AbstractBeanTest {

	private static final double DELTA = 1e-9;

	private final EpiWeek target = ClimateStatistics.epiWeekOf(LocalDate.now().minusDays(21));
	private final EpiWeek before = ClimateStatistics.epiWeekOf(LocalDate.now().minusDays(28));
	private final EpiWeek twoBefore = ClimateStatistics.epiWeekOf(LocalDate.now().minusDays(35));
	private final int year = ClimateStatistics.epiWeekOf(LocalDate.now()).getYear();
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private ClimateCaseFacade facade;

	@BeforeEach
	void setUpClimate() {
		assumeTrue(twoBefore.getYear() == year, "the weeks three to five weeks ago must lie in the current epi year");
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		user = loginWith(creator.createNationalUser());
		facade = getBean(ClimateCaseFacadeEjbLocal.class);
		// measles with aggregate reporting only; dengue with both, so its cases count
		configure(Disease.MEASLES, false, true);
		configure(Disease.DENGUE, true, true);
		getDiseaseConfigurationFacade().loadData();
	}

	@Test
	void testLabConfirmedReportsAddedAsSormasDoes() {

		FacilityReferenceDto secondFacility = FacilityFacadeEjb.toReferenceDto(
			creator.createFacility(
				"Second facility",
				getRegionService().getByUuid(rdcf.region.getUuid()),
				getDistrictService().getByUuid(rdcf.district.getUuid()),
				getCommunityService().getByUuid(rdcf.community.getUuid())));
		// two facilities of the district: added
		report(Disease.MEASLES, target, rdcf.district, rdcf.facility, 2);
		report(Disease.MEASLES, target, rdcf.district, secondFacility, 3);
		// a report for the whole district replaces its facility's
		report(Disease.MEASLES, twoBefore, rdcf.district, rdcf.facility, 4);
		report(Disease.MEASLES, twoBefore, rdcf.district, null, 7);
		// a report for the whole region replaces its districts' in the region's series only
		report(Disease.MEASLES, target, null, null, 9);
		// a measles case is not counted: the disease has no case surveillance
		createConfirmedCase(Disease.MEASLES, target);
		creator.createPopulationData(rdcf.region, rdcf.district, 100_000, new Date());

		ClimateCaseSeriesDto region = facade.getCaseSeries(criteria(Disease.MEASLES)).get(0);
		assertEquals(ClimateCaseCountSource.AGGREGATE_LAB_CONFIRMED, region.getCountSource());
		List<ClimateCaseWeekDto> weeks = region.getWeeks();
		assertEquals(7, weeks.get(0).getCount(), "the district's own report, not its facility's");
		assertTrue(weeks.get(0).isReported());
		assertEquals(0, weeks.get(0).getReportDateCount());
		assertFalse(weeks.get(1).isReported(), "a week without a report has no value");
		assertNull(weeks.get(1).getIncidence());
		assertFalse(weeks.get(1).isAboveUsualRange());
		assertEquals(9, weeks.get(2).getCount(), "the region's own report replaces its districts'");
		assertEquals(9.0, weeks.get(2).getIncidence(), DELTA);

		ClimateCaseSeriesDto district = facade.getCaseSeries(criteria(Disease.MEASLES).district(rdcf.district)).get(0);
		assertTrue(district.getWeeks().get(0).isReported());
		assertFalse(district.getWeeks().get(1).isReported());
		assertEquals(5, district.getWeeks().get(2).getCount(), "the district's facilities added; no region-level report in a district");
		assertEquals(
			5,
			facade.getCaseSeries(criteria(Disease.MEASLES).district(rdcf.district).classification(ClimateCaseClassificationFilter.ALL_BUT_NOT_A_CASE))
				.get(0)
				.getWeeks()
				.get(2)
				.getCount(),
			"the classification does not apply to aggregate reports");
	}

	@Test
	void testCaseSurveillanceWins() {

		createConfirmedCase(Disease.DENGUE, target);
		createConfirmedCase(Disease.DENGUE, target);
		report(Disease.DENGUE, target, rdcf.district, rdcf.facility, 50);

		ClimateCaseSeriesDto series = facade.getCaseSeries(criteria(Disease.DENGUE)).get(0);
		assertEquals(ClimateCaseCountSource.CASES, series.getCountSource());
		assertEquals(2, series.getWeeks().get(2).getCount(), "its cases, not its aggregate reports");
		assertTrue(series.getWeeks().get(1).isReported(), "a week without cases is 0, not a gap");
		assertEquals(0, series.getWeeks().get(1).getCount());
	}

	@Test
	void testUsualRangeLeavesOutWeeksWithoutReports() {

		// one report in the target week of each of the last three years; the window's other weeks have none
		report(Disease.MEASLES, new EpiWeek(year - 3, target.getWeek()), rdcf.district, null, 4);
		report(Disease.MEASLES, new EpiWeek(year - 2, target.getWeek()), rdcf.district, null, 6);
		report(Disease.MEASLES, new EpiWeek(year - 1, target.getWeek()), rdcf.district, null, 8);

		ClimateCaseSeriesDto series = facade.getCaseSeries(criteria(Disease.MEASLES).district(rdcf.district)).get(0);
		assertEquals(Arrays.asList(year - 3, year - 2, year - 1), series.getYearsWithCases(), "years with aggregate reports");
		ClimateCaseWeekDto week = series.getWeeks().get(2);
		assertNotNull(week.getUsualRange());
		assertEquals(6.0, week.getUsualRange().getP50(), DELTA, "4, 6 and 8, at least the minimum of 5; weeks without a report are not 0");
		assertFalse(week.isReported(), "no report this year");
		assertFalse(week.isAboveUsualRange());
		assertEquals(
			ClimateUsualRangeMissing.NOT_ENOUGH_HISTORY,
			facade.getCaseSeries(criteria(Disease.MEASLES).district(rdcf.district).weeks(new EpiWeek(year, 1), new EpiWeek(year, 1)))
				.get(0)
				.getWeeks()
				.get(0)
				.getUsualRangeMissing(),
			"a week whose window has a report in no reference year");
	}

	private ClimateCaseCriteria criteria(Disease disease) {
		return new ClimateCaseCriteria().region(rdcf.region).weeks(twoBefore, target).diseases(Collections.singletonList(disease));
	}

	private void configure(Disease disease, boolean caseSurveillance, boolean aggregateReporting) {
		DiseaseConfigurationDto configuration = getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		configuration.setActive(true);
		configuration.setCaseSurveillanceEnabled(caseSurveillance);
		configuration.setAggregateReportingEnabled(aggregateReporting);
		getDiseaseConfigurationFacade().saveDiseaseConfiguration(configuration);
	}

	/**
	 * Stores an aggregate report for the region (no district), a district (no facility) or a facility, as the mobile app or
	 * an older version may have stored a region-level one.
	 */
	private void report(Disease disease, EpiWeek week, DistrictReferenceDto district, FacilityReferenceDto facility, int labConfirmed) {
		executeInTransaction(em -> {
			AggregateReport report = new AggregateReport();
			report.setUuid(DataHelper.createUuid());
			report.setDisease(disease);
			report.setYear(week.getYear());
			report.setEpiWeek(week.getWeek());
			report.setRegion(getRegionService().getByUuid(rdcf.region.getUuid()));
			report.setDistrict(district != null ? getDistrictService().getByUuid(district.getUuid()) : null);
			report.setHealthFacility(facility != null ? getFacilityService().getByUuid(facility.getUuid()) : null);
			report.setNewCases(labConfirmed * 2);
			report.setLabConfirmations(labConfirmed);
			report.setDeaths(0);
			em.persist(report);
		});
	}

	private void createConfirmedCase(Disease disease, EpiWeek week) {
		Date day = ClimateCalendar.toDate(ClimateStatistics.firstDay(week).plusDays(2));
		creator.createCase(user.toReference(), rdcf, c -> {
			c.setDisease(disease);
			c.setCaseClassification(CaseClassification.CONFIRMED);
			c.setReportDate(day);
			c.getSymptoms().setOnsetDate(day);
		});
	}
}
