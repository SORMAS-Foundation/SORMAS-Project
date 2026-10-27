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

package de.symeda.sormas.ui.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateHistoryCoverageDto;
import de.symeda.sormas.api.climate.ClimateJobIndexDto;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.climate.HumidityMeasure;

class ClimateCaptionsTest {

	@Test
	void testPlaceNamesTheSharingPlaces() {

		ClimateObservationIndexDto district = new ClimateObservationIndexDto();
		district.setDistrictNames(Collections.singletonList("Ikeja"));
		assertEquals("Ikeja", ClimateCaptions.place(district));

		ClimateObservationIndexDto environment = new ClimateObservationIndexDto();
		environment.setEnvironmentNames(Collections.singletonList("Well A"));
		assertEquals("Well A (environment)", ClimateCaptions.place(environment));

		ClimateObservationIndexDto shared = new ClimateObservationIndexDto();
		shared.setDistrictNames(Collections.singletonList("Ikeja"));
		shared.setEnvironmentNames(Arrays.asList("Well A", "Well B"));
		assertEquals("Ikeja + 2 more", ClimateCaptions.place(shared));
		assertEquals(Arrays.asList("Ikeja", "Well A (environment)", "Well B (environment)"), ClimateCaptions.placeNames(shared));

		ClimateObservationIndexDto unlinked = new ClimateObservationIndexDto();
		unlinked.setLatitude(6.6);
		unlinked.setLongitude(3.35);
		assertEquals("Not linked (6.6, 3.35)", ClimateCaptions.place(unlinked));
	}

	@Test
	void testHistoryPeriodAndSources() {

		assertEquals("1 year 2 months", ClimateCaptions.period(Period.of(1, 2, 10)));
		assertEquals("3 years", ClimateCaptions.period(Period.of(3, 0, 0)));
		assertEquals("0 months", ClimateCaptions.period(Period.ZERO));

		ClimateHistoryCoverageDto coverage = new ClimateHistoryCoverageDto(null, null, true);
		coverage.addDays(2021, ClimateDataSource.IMPORT, 365, LocalDate.of(2021, 1, 1));
		coverage.addDays(2022, ClimateDataSource.IMPORT, 365, LocalDate.of(2022, 1, 1));
		coverage.addDays(2023, ClimateDataSource.IMPORT, 300, LocalDate.of(2023, 1, 1));
		coverage.addDays(2023, ClimateDataSource.OPENWEATHER, 65, LocalDate.of(2023, 10, 28));
		assertEquals(
			"2021–2022 " + ClimateDataSource.IMPORT + " · 2023 " + ClimateDataSource.IMPORT + " 300 days, " + ClimateDataSource.OPENWEATHER + " 65 days",
			ClimateCaptions.sources(coverage));
	}

	@Test
	void testJobScopeAndRows() {

		ClimateJobIndexDto relink = new ClimateJobIndexDto();
		relink.setJobType(ClimateJobType.RELINK);
		relink.setDistrictName("Ikeja");
		relink.setRowsAdded(1826);
		relink.setRowsSkipped(12);
		relink.setFromDate(new Date());
		relink.setToDate(new Date());
		assertEquals("Ikeja moved to a new point", ClimateJobsGrid.scope(relink));
		assertTrue(ClimateJobsGrid.rows(relink).startsWith(ClimateSummaryStrip.formatCount(1826) + " rows copied · 12 days not copied"));

		ClimateJobIndexDto backfill = new ClimateJobIndexDto();
		backfill.setJobType(ClimateJobType.BACKFILL);
		assertEquals("All districts", ClimateJobsGrid.scope(backfill));
		backfill.setRegionName("Lagos");
		backfill.setIncludeEnvironments(true);
		assertEquals("Lagos and environment points", ClimateJobsGrid.scope(backfill));
	}

	@Test
	void testValuesAreNeverShownAsZeroWhenMissing() {

		assertEquals("", ClimateCaptions.number(null));
		assertEquals("0", ClimateCaptions.number(0.0));
		assertEquals("26.4", ClimateCaptions.number(26.4));
		assertEquals("18.27", ClimateCaptions.number(18.2666));

		ClimateObservationIndexDto observation = new ClimateObservationIndexDto();
		assertEquals("", ClimateCaptions.humidity(observation));
		observation.setRelativeHumidity(84.0);
		observation.setHumidityMeasure(HumidityMeasure.NOON_READING);
		assertEquals("84 (" + HumidityMeasure.NOON_READING + ")", ClimateCaptions.humidity(observation));
	}
}
