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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Lone values on the climate lines get a dot (decision 148).
 */
class ClimateChartScriptTest {

	@Test
	void testIsLone() {

		List<Double> values = Arrays.asList(1.0, null, 2.0, null, 3.0, 4.0, null);
		assertTrue(ClimateChartScript.isLone(values, 0), "the first value with no value after it");
		assertFalse(ClimateChartScript.isLone(values, 1), "a missing value is no point");
		assertTrue(ClimateChartScript.isLone(values, 2), "between two missing values");
		assertFalse(ClimateChartScript.isLone(values, 4), "a value with a neighbour is drawn by the line");
		assertFalse(ClimateChartScript.isLone(values, 5));
		assertTrue(ClimateChartScript.isLone(Arrays.asList(null, 5.61), 1), "the last value with no value before it");
		assertTrue(ClimateChartScript.isLone(Arrays.asList(5.61), 0), "a single week");
	}
}
