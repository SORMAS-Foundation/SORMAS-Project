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

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateComparison;
import de.symeda.sormas.api.climate.ClimateDailySeriesDto;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDayDto;
import de.symeda.sormas.api.climate.ClimateRangePosition;
import de.symeda.sormas.api.climate.ClimateSeriesFacade;
import de.symeda.sormas.api.climate.ClimateSummaryCardDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateSeriesFacadeEjb.ClimateSeriesFacadeEjbLocal;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.infrastructure.district.District;

/**
 * The environment Climate data tab's daily series: own or district values and normals, and the summary cards. The
 * reference years are the complete calendar years before the current one.
 */
class ClimateDailySeriesTest extends AbstractBeanTest {

	private static final double DELTA = 1e-9;

	private final int year = LocalDate.now().getYear();
	private final LocalDate yesterday = LocalDate.now().minusDays(1);
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private ClimateSeriesFacade facade;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		user = creator.createSurveillanceSupervisor(rdcf);
		facade = getBean(ClimateSeriesFacadeEjbLocal.class);
		getBean(ClimateNormalsCache.class).clear();
	}

	@Test
	void testOwnValuesNormalsAndCardsAgainstNormal() {

		EnvironmentDto environment = createEnvironment(6.80, 3.50);
		Long location = environmentLocation(environment.getUuid());
		districtLocation(6.60, 3.35);
		fillYears(location, year - 4, year - 2, 1.0, 25.0, 70.0);
		fill(location, yesterday.minusDays(6), yesterday, 2.0, 26.0, 80.0);

		ClimateDailySeriesDto series = facade.getDailySeries(environment.toReference(), yesterday.minusDays(2), yesterday);

		assertFalse(series.isDistrictFallback());
		assertFalse(series.isDistrictNormals());
		assertEquals(year - 5, series.getFirstReferenceYear());
		assertEquals(LocalDate.of(year - 4, 1, 1), series.getHistoryFrom());
		assertEquals(3, series.getDays().size());
		ClimateDayDto day = series.getDays().get(2);
		assertEquals(yesterday, day.getDate());
		assertEquals(2.0, day.getRainfall(), DELTA);
		assertEquals(ClimateDataSource.OPENWEATHER, day.getSource());
		assertEquals(1.0, day.getRainfallNormal().getP50(), DELTA);
		assertEquals(25.0, day.getTemperatureNormal().getP90(), DELTA);
		assertEquals(HumidityMeasure.NOON_READING, day.getHumidityMeasure());
		assertEquals(70.0, day.getHumidityNormal().getP50(), DELTA);

		List<ClimateSummaryCardDto> cards = series.getCards();
		ClimateSummaryCardDto rainfall = cards.get(0);
		assertEquals(ClimateComparison.NORMAL, rainfall.getComparison());
		assertEquals(yesterday.minusDays(6), rainfall.getFirstDay());
		assertEquals(7, rainfall.getDays());
		assertEquals(14.0, rainfall.getValue(), DELTA);
		assertEquals(7.0, rainfall.getNormal().getP50(), DELTA, "the sum of the days' p50");
		assertEquals(100.0, rainfall.getPercentDifference(), DELTA);
		assertEquals(ClimateRangePosition.ABOVE, rainfall.getPosition());
		assertFalse(rainfall.isNormallyDry());

		ClimateSummaryCardDto temperature = cards.get(1);
		assertEquals(26.0, temperature.getValue(), DELTA);
		assertEquals(1.0, temperature.getDifference(), DELTA);

		ClimateSummaryCardDto humidity = cards.get(2);
		assertEquals(HumidityMeasure.NOON_READING, humidity.getHumidityMeasure());
		assertEquals(10.0, humidity.getDifference(), DELTA);
	}

	@Test
	void testDistrictNormalsWhenTheyCoverMoreDays() {

		EnvironmentDto environment = createEnvironment(6.80, 3.50);
		Long own = environmentLocation(environment.getUuid());
		Long district = districtLocation(6.60, 3.35);
		fillYears(district, year - 4, year - 2, 4.0, 25.0, 70.0);
		fill(own, yesterday.minusDays(6), yesterday, 2.0, 26.0, 80.0);

		ClimateDailySeriesDto series = facade.getDailySeries(environment.toReference(), yesterday.minusDays(6), yesterday);
		assertFalse(series.isDistrictFallback());
		assertTrue(series.isDistrictNormals());
		assertEquals(2.0, series.getDays().get(0).getRainfall(), DELTA, "values are the environment's own");
		assertEquals(4.0, series.getDays().get(0).getRainfallNormal().getP50(), DELTA, "normals are the district's");
		assertEquals(ClimateComparison.NORMAL, series.getCards().get(0).getComparison());
		assertEquals(-50.0, series.getCards().get(0).getPercentDifference(), DELTA);
	}

	@Test
	void testEnvironmentWithoutUsableGpsFallsBackToItsDistrict() {

		EnvironmentDto environment = createEnvironment(6.80, 3.50);
		Long district = districtLocation(6.60, 3.35);
		fill(district, yesterday.minusDays(1), yesterday, 3.0, 26.0, 80.0);
		executeInTransaction(em -> {
			Environment entity = getEnvironmentService().getByUuid(environment.getUuid());
			entity.setClimateLocation(null);
		});

		ClimateDailySeriesDto series = facade.getDailySeries(environment.toReference(), yesterday.minusDays(1), yesterday);
		assertTrue(series.isDistrictFallback());
		assertTrue(series.isDistrictNormals());
		assertEquals(rdcf.district, series.getDistrict());
		assertEquals(3.0, series.getDays().get(1).getRainfall(), DELTA);
		assertNull(series.getDays().get(1).getRainfallNormal());
		assertEquals(ClimateComparison.NOT_ENOUGH_DATA, series.getCards().get(0).getComparison(), "2 of 7 days");
		assertEquals(2, series.getCards().get(0).getDays());
		assertNull(series.getCards().get(0).getValue());
	}

	@Test
	void testCardsAgainstLastYearAndValuesOnly() {

		EnvironmentDto environment = createEnvironment(6.80, 3.50);
		Long location = environmentLocation(environment.getUuid());
		fillYears(location, year - 2, year - 1, 1.0, 25.0, 70.0);
		fill(location, yesterday.minusDays(6), yesterday, 2.0, 24.5, 75.0);

		List<ClimateSummaryCardDto> cards = facade.getDailySeries(environment.toReference(), yesterday, yesterday).getCards();
		assertEquals(ClimateComparison.LAST_YEAR, cards.get(0).getComparison(), "two reference years of the three needed");
		assertEquals(7.0, cards.get(0).getLastYear(), DELTA);
		assertEquals(100.0, cards.get(0).getPercentDifference(), DELTA);
		assertNull(cards.get(0).getNormal());
		assertEquals(-0.5, cards.get(1).getDifference(), DELTA);
		assertEquals(5.0, cards.get(2).getDifference(), DELTA);

		EnvironmentDto other = createEnvironment(7.80, 4.50);
		Long otherLocation = environmentLocation(other.getUuid());
		fill(otherLocation, yesterday.minusDays(6), yesterday, 2.0, 24.5, 75.0);
		ClimateSummaryCardDto valuesOnly = facade.getDailySeries(other.toReference(), yesterday, yesterday).getCards().get(0);
		assertEquals(ClimateComparison.VALUES_ONLY, valuesOnly.getComparison());
		assertEquals(14.0, valuesOnly.getValue(), DELTA);
	}

	@Test
	void testNormallyDry() {

		EnvironmentDto environment = createEnvironment(6.80, 3.50);
		Long location = environmentLocation(environment.getUuid());
		fillYears(location, year - 4, year - 2, 0.0, 25.0, 70.0);
		fill(location, yesterday.minusDays(6), yesterday, 1.0, 25.0, 70.0);

		ClimateSummaryCardDto rainfall = facade.getDailySeries(environment.toReference(), yesterday, yesterday).getCards().get(0);
		assertEquals(ClimateComparison.NORMAL, rainfall.getComparison());
		assertTrue(rainfall.isNormallyDry());
		assertNull(rainfall.getPercentDifference());
		assertEquals(7.0, rainfall.getValue(), DELTA);
		assertEquals(ClimateRangePosition.ABOVE, rainfall.getPosition());
	}

	private EnvironmentDto createEnvironment(double latitude, double longitude) {
		return creator.createEnvironment("Well A", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(latitude);
			e.getLocation().setLongitude(longitude);
		});
	}

	private Long environmentLocation(String environmentUuid) {
		return executeInTransaction(em -> {
			return getEnvironmentService().getByUuid(environmentUuid).getClimateLocation().getId();
		});
	}

	private Long districtLocation(double latitude, double longitude) {
		DistrictDto district = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		district.setClimateLatitude(latitude);
		district.setClimateLongitude(longitude);
		getDistrictFacade().save(district);
		return executeInTransaction(em -> {
			District entity = getDistrictService().getByUuid(rdcf.district.getUuid());
			if (entity.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(entity);
			}
			return entity.getClimateLocation().getId();
		});
	}

	private void fillYears(Long location, int fromYear, int toYear, double rainfall, double temperature, double humidity) {
		fill(location, LocalDate.of(fromYear, 1, 1), LocalDate.of(toYear, 12, 31), rainfall, temperature, humidity);
	}

	/**
	 * Writes one OpenWeather row a day, replacing any row the location already holds that day.
	 */
	private void fill(Long locationId, LocalDate from, LocalDate to, double rainfall, double temperature, double humidity) {
		executeInTransaction(em -> {
			ClimateLocation location = em.find(ClimateLocation.class, locationId);
			em.createQuery("DELETE FROM climateobservation o WHERE o.climateLocation = :location AND o.observationDate BETWEEN :from AND :to")
				.setParameter("location", location)
				.setParameter("from", ClimateCalendar.toDate(from))
				.setParameter("to", ClimateCalendar.toDate(to))
				.executeUpdate();
			for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
				ClimateObservation observation = new ClimateObservation();
				observation.setClimateLocation(location);
				observation.setObservationDate(ClimateCalendar.toDate(day));
				observation.setSource(ClimateDataSource.OPENWEATHER);
				observation.setRainfall(rainfall);
				observation.setTemperatureMean(temperature);
				observation.setRelativeHumidity(humidity);
				observation.setHumidityMeasure(HumidityMeasure.NOON_READING);
				em.persist(observation);
			}
		});
	}
}
