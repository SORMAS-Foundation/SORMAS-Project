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

package de.symeda.sormas.ui.epidata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.utils.UtilDate;

class EpiDataFormTest {

	private static final Date SYMPTOM_ONSET_DATE = UtilDate.from(LocalDate.of(2026, 9, 10));

	@Test
	void testTransmissionStartDateWithNegativeMinContagiousPeriodIsBeforeOnset() {
		Date startDate = EpiDataForm.calculateTransmissionStartDate(SYMPTOM_ONSET_DATE, -3);

		assertEquals(LocalDate.of(2026, 9, 7), UtilDate.toLocalDate(startDate));
	}

	@Test
	void testTransmissionStartDateWithPositiveMinContagiousPeriodIsAfterOnset() {
		Date startDate = EpiDataForm.calculateTransmissionStartDate(SYMPTOM_ONSET_DATE, 2);

		assertEquals(LocalDate.of(2026, 9, 12), UtilDate.toLocalDate(startDate));
	}

	@Test
	void testTransmissionStartDateWithZeroMinContagiousPeriodIsOnset() {
		Date startDate = EpiDataForm.calculateTransmissionStartDate(SYMPTOM_ONSET_DATE, 0);

		assertEquals(LocalDate.of(2026, 9, 10), UtilDate.toLocalDate(startDate));
	}

	@Test
	void testTransmissionStartDateCrossesMonthBoundary() {
		Date startDate = EpiDataForm.calculateTransmissionStartDate(UtilDate.from(LocalDate.of(2026, 3, 2)), -5);

		assertEquals(LocalDate.of(2026, 2, 25), UtilDate.toLocalDate(startDate));
	}
}
