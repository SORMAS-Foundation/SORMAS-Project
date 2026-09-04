/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.configuration.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CronExpressionSummaryTest {

	@Test
	public void describesADailySchedule() {
		assertEquals("Runs at 01:15 every day.", CronExpressionSummary.describe("0 15 1 * * *"));
	}

	@Test
	public void describesAMinuteInterval() {
		assertEquals("Runs every 10 minutes.", CronExpressionSummary.describe("0 */10 * * * *"));
	}

	@Test
	public void describesAnHourInterval() {
		assertEquals("Runs every 2 hours.", CronExpressionSummary.describe("0 0 */2 * * *"));
	}

	@Test
	public void describesAnHourlySchedule() {
		assertEquals("Runs hourly at :05.", CronExpressionSummary.describe("0 5 * * * *"));
	}

	@Test
	public void describesADisabledSchedule() {
		assertEquals("This job is disabled.", CronExpressionSummary.describe(""));
	}

	@Test
	public void fallsBackToAStructuralReadBackForUnrecognisedShapes() {

		String summary = CronExpressionSummary.describe("0 0 1,13 * * *");

		assertTrue(summary.contains("1,13"), summary);
		assertTrue(summary.contains("Hour"), summary);
	}

	@Test
	public void fallsBackForAnInvalidExpression() {
		assertTrue(CronExpressionSummary.describe("nightly").contains("nightly"));
	}
}
