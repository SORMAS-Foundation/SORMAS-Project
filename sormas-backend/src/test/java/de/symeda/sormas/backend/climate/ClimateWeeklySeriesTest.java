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

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateSeriesCriteria;
import de.symeda.sormas.api.climate.ClimateSeriesFacade;
import de.symeda.sormas.api.climate.ClimateVariable;
import de.symeda.sormas.api.climate.ClimateWeekDto;
import de.symeda.sormas.api.climate.ClimateWeeklySeriesDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateSeriesFacadeEjb.ClimateSeriesFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

/**
 * Weekly climate series: roll-up, region values, normals and their cache, unusual weeks and last year's values. Years are
 * relative to today, because the reference years are the complete epi years before the current one.
 */
class ClimateWeeklySeriesTest extends AbstractBeanTest {

	private static final double DELTA = 1e-9;

	private final int year = ClimateStatistics.epiWeekOf(LocalDate.now()).getYear();
	private TestDataCreator.RDCF rdcf;
	private ClimateSeriesFacade facade;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		creator.createSurveillanceSupervisor(rdcf);
		facade = getBean(ClimateSeriesFacadeEjbLocal.class);
		getBean(ClimateNormalsCache.class).clear();
	}

	@Test
	void testDistrictNormalsUnusualWeeksAndLastYear() {

		Long location = districtLocation(rdcf.district.getUuid(), 6.60, 3.35);
		// weeks 9 to 11 of three reference years: 7, 14 and 21 mm a week; 25, 26 and 27 °C
		for (int i = 1; i <= 3; i++) {
			for (int week = 9; week <= 11; week++) {
				fillWeek(location, new EpiWeek(year - 4 + i, week), ClimateDataSource.OPENWEATHER, i, 24.0 + i, 70.0, HumidityMeasure.NOON_READING);
			}
		}
		fillWeek(location, new EpiWeek(year, 10), ClimateDataSource.OPENWEATHER, 10.0, 26.5, 90.0, HumidityMeasure.NOON_READING);
		fillWeek(location, new EpiWeek(year, 11), ClimateDataSource.OPENWEATHER, 2.0, 30.0, 60.0, HumidityMeasure.NOON_READING);

		ClimateWeeklySeriesDto series = facade.getWeeklySeries(districtCriteria(new EpiWeek(year, 10), new EpiWeek(year, 11)));

		assertTrue(series.isHasPoint());
		assertEquals(year - 5, series.getFirstReferenceYear());
		assertEquals(year - 1, series.getLastReferenceYear());
		assertEquals(ClimateStatistics.firstDay(new EpiWeek(year - 3, 9)), series.getHistoryFrom());

		ClimateWeekDto week10 = series.getWeeks().get(0);
		assertEquals(70.0, week10.getRainfall().getValue(), DELTA);
		assertEquals(7, week10.getRainfall().getDays());
		assertNull(week10.getRainfall().getDistricts());
		assertEquals(7.0, week10.getRainfall().getNormal().getP10(), DELTA);
		assertEquals(14.0, week10.getRainfall().getNormal().getP50(), DELTA);
		assertEquals(21.0, week10.getRainfall().getNormal().getP90(), DELTA);
		assertTrue(week10.getRainfall().isUnusual(), "70 mm is above p90 and above the 60 mm minimum");
		assertEquals(21.0, week10.getRainfall().getLastYear(), DELTA);
		assertFalse(week10.getTemperature().isUnusual(), "26.5 °C is below p90 (27 °C)");
		assertEquals(27.0, week10.getTemperature().getNormal().getP90(), DELTA);
		assertEquals(HumidityMeasure.NOON_READING, week10.getHumidityMeasure());
		assertTrue(week10.getHumidity().isUnusual());

		ClimateWeekDto week11 = series.getWeeks().get(1);
		assertFalse(week11.getRainfall().isUnusual(), "14 mm is not above p90");
		assertTrue(week11.getTemperature().isUnusual());
		assertEquals(1, series.countUnusualWeeks(ClimateVariable.RAINFALL));
		assertEquals(84.0, series.getRainfallTotal(), DELTA);
		assertEquals(28.25, series.getTemperatureMean(), DELTA);
		assertEquals(HumidityMeasure.NOON_READING, series.getRangeHumidityMeasure());
	}

	@Test
	void testTooFewReferenceYearsGiveNoNormal() {

		Long location = districtLocation(rdcf.district.getUuid(), 6.60, 3.35);
		fillWeek(location, new EpiWeek(year - 2, 10), ClimateDataSource.IMPORT, 1.0, 25.0, 70.0, HumidityMeasure.DAILY_MEAN);
		fillWeek(location, new EpiWeek(year - 1, 10), ClimateDataSource.IMPORT, 1.0, 25.0, 70.0, HumidityMeasure.DAILY_MEAN);
		fillWeek(location, new EpiWeek(year, 10), ClimateDataSource.IMPORT, 20.0, 25.0, 70.0, HumidityMeasure.DAILY_MEAN);

		ClimateWeekDto week = facade.getWeeklySeries(districtCriteria(new EpiWeek(year, 10), new EpiWeek(year, 10))).getWeeks().get(0);
		assertNull(week.getRainfall().getNormal(), "two reference years of the three needed");
		assertFalse(week.getRainfall().isUnusual());
		assertEquals(7.0, week.getRainfall().getLastYear(), DELTA);
	}

	@Test
	void testHumidityIsComparedOnlyWithTheSameMeasure() {

		Long location = districtLocation(rdcf.district.getUuid(), 6.60, 3.35);
		for (int i = 1; i <= 3; i++) {
			fillWeek(location, new EpiWeek(year - i, 10), ClimateDataSource.OPENWEATHER, 1.0, 25.0, 60.0, HumidityMeasure.NOON_READING);
		}
		fillWeek(location, new EpiWeek(year, 10), ClimateDataSource.IMPORT, 1.0, 25.0, 95.0, HumidityMeasure.DAILY_MEAN);
		fillWeek(location, new EpiWeek(year, 11), ClimateDataSource.IMPORT, 1.0, 25.0, null, null);

		ClimateWeeklySeriesDto series = facade.getWeeklySeries(districtCriteria(new EpiWeek(year, 10), new EpiWeek(year, 11)));
		ClimateWeekDto week10 = series.getWeeks().get(0);
		assertEquals(HumidityMeasure.DAILY_MEAN, week10.getHumidityMeasure());
		assertNull(week10.getHumidity().getNormal(), "the normals are noon readings");
		assertNull(week10.getHumidity().getLastYear(), "last year's value is a noon reading");
		assertFalse(week10.getHumidity().isUnusual());

		ClimateWeekDto week11 = series.getWeeks().get(1);
		assertNull(week11.getHumidity().getValue());
		assertEquals(HumidityMeasure.NOON_READING, week11.getHumidityMeasure(), "a gap is drawn against the normal there is");
		assertNotNull(week11.getHumidity().getNormal());
	}

	@Test
	void testRegionNeedsHalfOfItsDistricts() {

		Region region = getRegionService().getByUuid(rdcf.region.getUuid());
		District second = creator.createDistrict("Second", region);
		District third = creator.createDistrict("Third", region);
		Long first = districtLocation(rdcf.district.getUuid(), 6.60, 3.35);
		Long secondLocation = districtLocation(second.getUuid(), 7.60, 3.35);
		districtLocation(third.getUuid(), 8.60, 3.35);

		fillWeek(first, new EpiWeek(year, 10), ClimateDataSource.IMPORT, 1.0, 20.0, 80.0, HumidityMeasure.DAILY_MEAN);
		fillWeek(secondLocation, new EpiWeek(year, 10), ClimateDataSource.IMPORT, 3.0, 30.0, 60.0, HumidityMeasure.NOON_READING);
		fillWeek(first, new EpiWeek(year, 11), ClimateDataSource.IMPORT, 1.0, 20.0, 80.0, HumidityMeasure.DAILY_MEAN);

		ClimateWeeklySeriesDto series = facade.getWeeklySeries(
			new ClimateSeriesCriteria().region(rdcf.region).weeks(new EpiWeek(year, 10), new EpiWeek(year, 11)).includeDistricts(true));

		assertNull(series.getDistrict());
		ClimateWeekDto week10 = series.getWeeks().get(0);
		assertEquals(14.0, week10.getRainfall().getValue(), DELTA, "mean of 7 and 21 mm");
		assertEquals(2, week10.getRainfall().getDistricts());
		assertNull(week10.getRainfall().getDays());
		assertEquals(25.0, week10.getTemperature().getValue(), DELTA);
		assertNull(week10.getHumidity().getValue(), "one daily mean and one noon reading: neither reaches half of three districts");

		assertNull(series.getWeeks().get(1).getRainfall().getValue(), "one district of three");
		assertNull(series.getHistoryFrom(), "the third district holds nothing");

		assertEquals(3, series.getDistrictSeries().size());
		assertEquals("Second", series.getDistrictSeries().get(1).getDistrict().getCaption());
		assertEquals(21.0, series.getDistrictSeries().get(1).getWeeks().get(0).getRainfall().getValue(), DELTA);
	}

	@Test
	void testCompletenessCountsPastDaysPerVariable() {

		Long location = districtLocation(rdcf.district.getUuid(), 6.60, 3.35);
		EpiWeek lastWeek = ClimateStatistics.epiWeekOf(LocalDate.now().minusDays(7));
		LocalDate monday = ClimateStatistics.firstDay(lastWeek);
		for (int i = 0; i < 6; i++) {
			createObservation(location, monday.plusDays(i), ClimateDataSource.IMPORT, 1.0, i < 3 ? 25.0 : null, null, null);
		}

		ClimateWeeklySeriesDto series = facade.getWeeklySeries(districtCriteria(lastWeek, lastWeek));
		assertEquals(6 / 7.0, series.getCompleteness(ClimateVariable.RAINFALL), DELTA);
		assertEquals(3 / 7.0, series.getCompleteness(ClimateVariable.TEMPERATURE), DELTA);
		assertEquals(0.0, series.getCompleteness(ClimateVariable.HUMIDITY), DELTA);
		ClimateWeekDto week = series.getWeeks().get(0);
		assertEquals(6.0, week.getRainfall().getValue(), DELTA);
		assertNull(week.getTemperature().getValue());
		assertEquals(3, week.getTemperature().getDays());

		EpiWeek nextWeek = ClimateStatistics.epiWeekOf(LocalDate.now().plusDays(7));
		assertNull(facade.getWeeklySeries(districtCriteria(nextWeek, nextWeek)).getCompleteness(ClimateVariable.RAINFALL));
	}

	@Test
	void testCacheNoticesAddedChangedAndDeletedRows() throws InterruptedException {

		Long location = districtLocation(rdcf.district.getUuid(), 6.60, 3.35);
		for (int i = 1; i <= 3; i++) {
			fillWeek(location, new EpiWeek(year - i, 10), ClimateDataSource.IMPORT, i, 25.0, 70.0, HumidityMeasure.DAILY_MEAN);
		}
		ClimateSeriesCriteria criteria = districtCriteria(new EpiWeek(year, 10), new EpiWeek(year, 10));
		// weeks of 7, 14 and 21 mm: p90 = 14 + 0.8 × 7
		assertEquals(19.6, p90(criteria), DELTA);
		assertEquals(19.6, p90(criteria), DELTA, "from the cache");

		LocalDate day = ClimateStatistics.firstDay(new EpiWeek(year - 3, 10));
		executeInTransaction(em -> {
			em.createQuery("UPDATE climateobservation o SET o.rainfall = 10 WHERE o.climateLocation.id = :location AND o.observationDate = :day")
				.setParameter("location", location)
				.setParameter("day", ClimateCalendar.toDate(day))
				.executeUpdate();
		});
		assertEquals(19.6, p90(criteria), DELTA, "an update that leaves the change date alone is not seen");
		Thread.sleep(10);
		executeInTransaction(em -> {
			ClimateObservation row = em
				.createQuery("SELECT o FROM climateobservation o WHERE o.climateLocation.id = :location AND o.observationDate = :day", ClimateObservation.class)
				.setParameter("location", location)
				.setParameter("day", ClimateCalendar.toDate(day))
				.getSingleResult();
			row.setRainfall(100.0);
		});
		assertEquals(97.2, p90(criteria), DELTA, "118 mm in that week now: 14 + 0.8 × 104");

		executeInTransaction(em -> {
			em.createQuery("DELETE FROM climateobservation o WHERE o.climateLocation.id = :location AND o.observationDate = :day")
				.setParameter("location", location)
				.setParameter("day", ClimateCalendar.toDate(day))
				.executeUpdate();
		});
		assertEquals(17.2, p90(criteria), DELTA, "the week of 6 days is still complete, at 18 mm");
	}

	@Test
	void testDistrictWithoutPointOrData() {

		ClimateWeeklySeriesDto series = facade.getWeeklySeries(districtCriteria(new EpiWeek(year, 10), new EpiWeek(year, 10)));
		assertFalse(series.isHasPoint());
		assertNull(series.getHistoryFrom());
		assertNull(series.getWeeks().get(0).getRainfall().getValue());
		assertNull(series.getRainfallTotal());
		assertFalse(series.hasNormals(ClimateVariable.RAINFALL));
	}

	private double p90(ClimateSeriesCriteria criteria) {
		return facade.getWeeklySeries(criteria).getWeeks().get(0).getRainfall().getNormal().getP90();
	}

	private ClimateSeriesCriteria districtCriteria(EpiWeek from, EpiWeek to) {
		return new ClimateSeriesCriteria().region(rdcf.region).district(rdcf.district).weeks(from, to);
	}

	/**
	 * Gives the district a climate point and returns its location.
	 */
	private Long districtLocation(String districtUuid, double latitude, double longitude) {
		DistrictDto district = getDistrictFacade().getByUuid(districtUuid);
		district.setClimateLatitude(latitude);
		district.setClimateLongitude(longitude);
		getDistrictFacade().save(district);
		return executeInTransaction(em -> {
			District entity = getDistrictService().getByUuid(districtUuid);
			if (entity.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(entity);
			}
			return entity.getClimateLocation().getId();
		});
	}

	private void fillWeek(
		Long location,
		EpiWeek week,
		ClimateDataSource source,
		double dailyRainfall,
		double temperature,
		Double humidity,
		HumidityMeasure measure) {

		LocalDate monday = ClimateStatistics.firstDay(week);
		for (int i = 0; i < 7; i++) {
			createObservation(location, monday.plusDays(i), source, dailyRainfall, temperature, humidity, measure);
		}
	}

	private void createObservation(
		Long locationId,
		LocalDate day,
		ClimateDataSource source,
		Double rainfall,
		Double temperature,
		Double humidity,
		HumidityMeasure measure) {

		executeInTransaction(em -> {
			ClimateObservation observation = new ClimateObservation();
			observation.setClimateLocation(em.find(ClimateLocation.class, locationId));
			observation.setObservationDate(ClimateCalendar.toDate(day));
			observation.setSource(source);
			observation.setRainfall(rainfall);
			observation.setTemperatureMean(temperature);
			observation.setTemperatureMin(temperature != null ? temperature - 5 : null);
			observation.setTemperatureMax(temperature != null ? temperature + 5 : null);
			observation.setRelativeHumidity(humidity);
			observation.setHumidityMeasure(measure);
			em.persist(observation);
		});
	}
}
