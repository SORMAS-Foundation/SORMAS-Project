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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.junit.jupiter.api.Test;

class ClimateHistoryCoverageDtoTest {

	private static final LocalDate TODAY = LocalDate.of(2026, 10, 27);

	@Test
	void testSpansMergeConsecutiveYearsOfOneSource() {

		ClimateHistoryCoverageDto coverage = new ClimateHistoryCoverageDto(null, null, true);
		coverage.addDays(2021, ClimateDataSource.IMPORT, 365, LocalDate.of(2021, 1, 1));
		coverage.addDays(2022, ClimateDataSource.IMPORT, 365, LocalDate.of(2022, 1, 1));
		coverage.addDays(2023, ClimateDataSource.IMPORT, 300, LocalDate.of(2023, 1, 1));
		coverage.addDays(2023, ClimateDataSource.OPENWEATHER, 65, LocalDate.of(2023, 10, 28));
		coverage.addDays(2024, ClimateDataSource.OPENWEATHER, 366, LocalDate.of(2024, 1, 1));
		coverage.addDays(2025, ClimateDataSource.OPENWEATHER, 365, LocalDate.of(2025, 1, 1));

		List<ClimateHistoryCoverageDto.SourceSpan> spans = coverage.getSourceSpans();
		assertEquals(3, spans.size());
		assertEquals(2021, spans.get(0).getFromYear());
		assertEquals(2022, spans.get(0).getToYear());
		assertEquals(730, spans.get(0).getDaysBySource().get(ClimateDataSource.IMPORT));
		assertFalse(spans.get(1).isSingleSource(), "a year with two sources stands alone");
		assertEquals(2024, spans.get(2).getFromYear());
		assertEquals(2025, spans.get(2).getToYear());

		assertEquals(LocalDate.of(2021, 1, 1), coverage.getHistoryFrom());
		assertEquals(1826, coverage.getDaysHeld());
	}

	@Test
	void testHeldPeriodAndCompleteness() {

		ClimateHistoryCoverageDto coverage = new ClimateHistoryCoverageDto(null, null, true);
		assertEquals(Period.ZERO, coverage.getHeldPeriod(TODAY));
		assertNull(coverage.getCompleteness(TODAY));

		coverage.addDays(2025, ClimateDataSource.OPENWEATHER, 50, LocalDate.of(2025, 8, 27));
		coverage.addDays(2026, ClimateDataSource.OPENWEATHER, 300, LocalDate.of(2026, 1, 1));

		assertEquals(Period.of(1, 2, 0), coverage.getHeldPeriod(TODAY), "1 year 2 months of history");
		assertFalse(coverage.hasEnoughHistory(TODAY, 3));
		assertTrue(coverage.hasEnoughHistory(TODAY, 1));
		assertEquals(350 / 426.0, coverage.getCompleteness(TODAY), 0.0001);
	}
}
