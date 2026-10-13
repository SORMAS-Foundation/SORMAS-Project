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
package de.symeda.sormas.api.event.sevenoneseven;

import static de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus.DATA_ERROR;
import static de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus.MISSING;
import static de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus.OVER_TARGET;
import static de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus.WITHIN_TARGET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class Event717SummaryDtoTest {

	@Test
	public void testPercentageOnlyCountsEvaluable() {

		// 2 within, 1 over, 4 missing, 1 incomplete, 1 data error, 3 not applicable
		Event717OutcomeCountsDto counts = new Event717OutcomeCountsDto(2, 1, 4, 1, 1, 3);
		assertEquals(3, counts.getEvaluable());
		assertEquals(Integer.valueOf(67), counts.getPercentageWithinTarget());

		assertEquals(Integer.valueOf(100), new Event717OutcomeCountsDto(5, 0, 2, 0, 0, 0).getPercentageWithinTarget());
		assertEquals(Integer.valueOf(0), new Event717OutcomeCountsDto(0, 3, 0, 0, 0, 0).getPercentageWithinTarget());
		assertNull(new Event717OutcomeCountsDto(0, 0, 4, 1, 1, 3).getPercentageWithinTarget());
	}

	@Test
	public void testEmptySummary() {

		Event717SummaryDto summary = new Event717SummaryDto(0);
		assertEquals(0, summary.getAssessedEvents());
		assertNull(summary.getAllTargets().getPercentageWithinTarget());
		assertNull(summary.getInterval(Event717Interval.DETECTION).getPercentageWithinTarget());
		assertEquals(0, summary.getEarlyResponseAction(Event717EarlyResponseAction.COORDINATION).getNotApplicable());
	}

	@Test
	public void testEarlyResponseActionStatus() {

		assertEquals(MISSING, Event717TimelinessCalculator.calculateEarlyResponseActionStatus(null));
		assertEquals(DATA_ERROR, Event717TimelinessCalculator.calculateEarlyResponseActionStatus(-1));
		assertEquals(WITHIN_TARGET, Event717TimelinessCalculator.calculateEarlyResponseActionStatus(0));
		assertEquals(WITHIN_TARGET, Event717TimelinessCalculator.calculateEarlyResponseActionStatus(7));
		assertEquals(OVER_TARGET, Event717TimelinessCalculator.calculateEarlyResponseActionStatus(8));
	}
}
