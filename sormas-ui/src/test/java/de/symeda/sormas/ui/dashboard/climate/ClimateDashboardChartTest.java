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

package de.symeda.sormas.ui.dashboard.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.ui.climate.ClimateChartScript;

class ClimateDashboardChartTest {

	@Test
	void testLoneWeekGetsADot() {
		// decision 148: one complete week between weeks without data is drawn as a dot
		assertEquals(
			"null,{y:5.61," + ClimateChartScript.LONE_MARKER + "},null,1.00,2.00,",
			ClimateDashboardChart.lineData(Arrays.asList(null, 5.61, null, 1.0, 2.0)));
	}
}
