/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/

package de.symeda.sormas.backend.epipulse.disease;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.epipulse.EpipulseImmunizationCheckDto;
import de.symeda.sormas.api.epipulse.EpipulseVaccinationCheckDto;

/**
 * The seam between the two vaccination aggregates and the rules that read them.
 */
public class VaccinationFieldsTest {

	// --- doses ---

	@Test
	@DisplayName("a full dose record parses into all three fields")
	public void aFullDoseRecordParses() {

		List<EpipulseVaccinationCheckDto> doses = VaccinationFields.parseVaccinations("2023-05-01|2023-05-03|417");

		assertEquals(1, doses.size());
		assertEquals("2023-05-01", onDay(doses.get(0).getVaccinationDate()));
		assertEquals("2023-05-03", onDay(doses.get(0).getReportDate()));
		assertEquals(Long.valueOf(417), doses.get(0).getImmunizationId());
	}

	@Test
	@DisplayName("a dose with nothing but a date and its course still parses")
	public void aSparseDoseRecordParses() {

		// a dose administered on a known date but never separately reported
		List<EpipulseVaccinationCheckDto> doses = VaccinationFields.parseVaccinations("2023-05-01||417");

		assertEquals(1, doses.size());
		assertEquals("2023-05-01", onDay(doses.get(0).getVaccinationDate()));
		assertNull(doses.get(0).getReportDate());
		assertEquals(Long.valueOf(417), doses.get(0).getImmunizationId());
	}

	@Test
	@DisplayName("doses keep the course they were given under")
	public void dosesKeepTheirCourse() {

		// the flat list says nothing about courses on its own; this is what groups the first two
		// doses under one and the third under another
		List<EpipulseVaccinationCheckDto> doses = VaccinationFields.parseVaccinations("2023-07-01||12#2020-02-01||11#2020-01-01||11");

		assertEquals(3, doses.size());
		assertEquals(Long.valueOf(12), doses.get(0).getImmunizationId());
		assertEquals(Long.valueOf(11), doses.get(1).getImmunizationId());
		assertEquals(Long.valueOf(11), doses.get(2).getImmunizationId());
		assertEquals("2023-07-01", onDay(doses.get(0).getVaccinationDate()));
	}

	// --- courses ---

	@Test
	@DisplayName("a full course record parses into all three fields")
	public void aFullCourseRecordParses() {

		List<EpipulseImmunizationCheckDto> courses = VaccinationFields.parseImmunizations("417|2024-02-01|9999-12-31|2");

		assertEquals(1, courses.size());
		assertEquals(Long.valueOf(417), courses.get(0).getId());
		assertEquals("2024-02-01", onDay(courses.get(0).getValidFrom()));
		assertEquals("9999-12-31", onDay(courses.get(0).getValidUntil()));
		assertEquals(Integer.valueOf(2), courses.get(0).getNumberOfDoses());
	}

	@Test
	@DisplayName("a course with no dose count keeps its trailing empty field")
	public void aCourseWithoutADoseCountParses() {

		// this is the record that becomes UNKDOSE, so it must survive the split rather than be
		// dropped one field short
		List<EpipulseImmunizationCheckDto> courses = VaccinationFields.parseImmunizations("417|2024-02-01|9999-12-31|");

		assertEquals(1, courses.size());
		assertEquals("2024-02-01", onDay(courses.get(0).getValidFrom()));
		assertNull(courses.get(0).getNumberOfDoses());
	}

	@Test
	@DisplayName("a course with no validity period parses, and is rejected later by the rule")
	public void aCourseWithoutAValidityPeriodParses() {

		// parsing and qualifying are separate jobs: the record is read faithfully here and
		// discarded by vaccinationStatus, where the reason can be stated
		List<EpipulseImmunizationCheckDto> courses = VaccinationFields.parseImmunizations("417|||3");

		assertEquals(1, courses.size());
		assertEquals(Long.valueOf(417), courses.get(0).getId());
		assertNull(courses.get(0).getValidFrom());
		assertNull(courses.get(0).getValidUntil());
		assertEquals(Integer.valueOf(3), courses.get(0).getNumberOfDoses());
	}

	@Test
	@DisplayName("several courses come back separately rather than merged")
	public void severalCoursesStaySeparate() {

		List<EpipulseImmunizationCheckDto> courses = VaccinationFields.parseImmunizations("12|2024-02-01|9999-12-31|1#11|2019-06-01|9999-12-31|3");

		assertEquals(2, courses.size());
		assertEquals(Long.valueOf(12), courses.get(0).getId());
		assertEquals(Integer.valueOf(1), courses.get(0).getNumberOfDoses());
		assertEquals(Long.valueOf(11), courses.get(1).getId());
		assertEquals(Integer.valueOf(3), courses.get(1).getNumberOfDoses());
	}

	@Test
	@DisplayName("nothing recorded is an empty list, not a null one")
	public void nothingRecordedIsAnEmptyList() {

		assertTrue(VaccinationFields.parseVaccinations(null).isEmpty());
		assertTrue(VaccinationFields.parseVaccinations("").isEmpty());
		assertTrue(VaccinationFields.parseImmunizations(null).isEmpty());
		assertTrue(VaccinationFields.parseImmunizations("").isEmpty());
	}

	/**
	 * The day a parsed date landed on. Compared as text because {@code DateHelper.getDateZero}
	 * zeroes the time of day but not the milliseconds, so two dates built for the same day are
	 * never {@code equals}.
	 */
	private static String onDay(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
}
