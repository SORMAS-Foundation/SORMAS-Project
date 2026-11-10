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

package de.symeda.sormas.api.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ClimateObservationValidationTest {

	private static final LocalDate TODAY = LocalDate.of(2026, 10, 22);

	@Test
	void testValidEntry() {
		assertNull(ClimateObservationValidation.validate(entry(), TODAY));
	}

	@Test
	void testExactlyOneLocationKey() {

		ClimateObservationEntryDto none = entry();
		none.setDistrictUuid(null);
		assertNotNull(ClimateObservationValidation.validate(none, TODAY));

		ClimateObservationEntryDto two = entry();
		two.setEnvironmentUuid("ENV");
		assertNotNull(ClimateObservationValidation.validate(two, TODAY));
	}

	@Test
	void testDateMustBeAnEndedDay() {

		ClimateObservationEntryDto entry = entry();
		entry.setDate("14/07/2025");
		assertNotNull(ClimateObservationValidation.validate(entry, TODAY), "only yyyy-mm-dd");

		entry.setDate("2026-10-22");
		assertNotNull(ClimateObservationValidation.validate(entry, TODAY), "today has not ended");

		entry.setDate("2026-10-21");
		assertNull(ClimateObservationValidation.validate(entry, TODAY));
	}

	@Test
	void testAtLeastOneValue() {

		ClimateObservationEntryDto entry = entry();
		entry.setTemperatureMin(null);
		entry.setTemperatureMax(null);
		entry.setTemperatureMean(null);
		entry.setRainfall(null);
		entry.setRelativeHumidity(null);
		assertNotNull(ClimateObservationValidation.validate(entry, TODAY));

		entry.setRainfall(0.0);
		assertNull(ClimateObservationValidation.validate(entry, TODAY), "a dry day is a value");
	}

	@Test
	void testLimitsRejectOnlyImpossibleValues() {

		ClimateObservationEntryDto entry = entry();
		entry.setTemperatureMin(-55.0);
		entry.setRainfall(950.0);
		assertNull(ClimateObservationValidation.validate(entry, TODAY), "real extremes are kept");

		entry.setTemperatureMin(-61.0);
		assertNotNull(ClimateObservationValidation.validate(entry, TODAY));

		entry = entry();
		entry.setRainfall(1001.0);
		assertNotNull(ClimateObservationValidation.validate(entry, TODAY));

		entry = entry();
		entry.setRainfall(-1.0);
		assertNotNull(ClimateObservationValidation.validate(entry, TODAY));

		entry = entry();
		entry.setRelativeHumidity(101.0);
		assertTrue(ClimateObservationValidation.validate(entry, TODAY).contains("100 %"));
	}

	@Test
	void testTemperatureOrderAllowsRounding() {

		ClimateObservationEntryDto entry = entry();
		entry.setTemperatureMin(26.45);
		entry.setTemperatureMean(26.4);
		assertNull(ClimateObservationValidation.validate(entry, TODAY), "0.05 °C apart: rounding");

		entry.setTemperatureMin(26.6);
		assertNotNull(ClimateObservationValidation.validate(entry, TODAY));
	}

	@Test
	void testHumidityNeedsItsMeasure() {

		ClimateObservationEntryDto entry = entry();
		entry.setHumidityMeasure(null);
		assertNotNull(ClimateObservationValidation.validate(entry, TODAY));

		entry.setRelativeHumidity(null);
		assertNull(ClimateObservationValidation.validate(entry, TODAY));
	}

	@Test
	void testMeanFilledFromMinAndMax() {

		ClimateObservationEntryDto entry = entry();
		entry.setTemperatureMean(null);
		ClimateObservationValidation.fillMeanTemperature(entry);
		assertEquals(26.7, entry.getTemperatureMean());

		entry.setTemperatureMean(25.0);
		ClimateObservationValidation.fillMeanTemperature(entry);
		assertEquals(25.0, entry.getTemperatureMean(), "a given mean is kept");
	}

	private static ClimateObservationEntryDto entry() {
		ClimateObservationEntryDto entry = new ClimateObservationEntryDto();
		entry.setDistrictUuid("DISTRICT");
		entry.setDate("2025-07-14");
		entry.setTemperatureMin(23.8);
		entry.setTemperatureMax(29.6);
		entry.setTemperatureMean(26.4);
		entry.setRainfall(18.2);
		entry.setRelativeHumidity(84.0);
		entry.setHumidityMeasure(HumidityMeasure.DAILY_MEAN);
		return entry;
	}
}
