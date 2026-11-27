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
import de.symeda.sormas.api.immunization.MeansOfImmunization;

/**
 * The seam between the two vaccination aggregates and the rules that read them.
 */
public class VaccinationFieldsTest {

	// --- doses ---

	@Test
	@DisplayName("a full dose record parses into its immunization id and date")
	public void aFullDoseRecordParses() {

		List<EpipulseVaccinationCheckDto> doses = VaccinationFields.parseVaccinations("42|2023-05-01");

		assertEquals(1, doses.size());
		assertEquals(Long.valueOf(42), doses.get(0).getImmunizationId());
		assertEquals("2023-05-01", onDay(doses.get(0).getVaccinationDate()));
	}

	@Test
	@DisplayName("a dose with no date of administration keeps its immunization id")
	public void aDoseWithoutAnAdministrationDateParses() {

		List<EpipulseVaccinationCheckDto> doses = VaccinationFields.parseVaccinations("42|");

		assertEquals(1, doses.size());
		assertEquals(Long.valueOf(42), doses.get(0).getImmunizationId());
		assertNull(doses.get(0).getVaccinationDate());
	}

	@Test
	@DisplayName("several doses come back separately, in the order the aggregate wrote them")
	public void severalDosesStaySeparate() {

		List<EpipulseVaccinationCheckDto> doses = VaccinationFields.parseVaccinations("7|2023-07-01#7|2020-02-01#8|");

		assertEquals(3, doses.size());
		assertEquals("2023-07-01", onDay(doses.get(0).getVaccinationDate()));
		assertEquals("2020-02-01", onDay(doses.get(1).getVaccinationDate()));
		assertNull(doses.get(2).getVaccinationDate());
		assertEquals(Long.valueOf(8), doses.get(2).getImmunizationId());
	}

	// --- immunizations ---

	@Test
	@DisplayName("a full immunization record parses into its id, means and validity dates")
	public void aFullImmunizationRecordParses() {

		List<EpipulseImmunizationCheckDto> immunizations = VaccinationFields.parseImmunizations("42|VACCINATION|2024-02-01|2034-02-01");

		assertEquals(1, immunizations.size());
		assertEquals(Long.valueOf(42), immunizations.get(0).getId());
		assertEquals(MeansOfImmunization.VACCINATION, immunizations.get(0).getMeansOfImmunization());
		assertEquals("2024-02-01", onDay(immunizations.get(0).getValidFrom()));
		assertEquals("2034-02-01", onDay(immunizations.get(0).getValidUntil()));
	}

	@Test
	@DisplayName("an immunization with no validity dates parses with both left empty")
	public void anImmunizationWithoutValidityDatesParses() {

		List<EpipulseImmunizationCheckDto> immunizations = VaccinationFields.parseImmunizations("42|NOT_IMMUNIZED||");

		assertEquals(1, immunizations.size());
		assertEquals(MeansOfImmunization.NOT_IMMUNIZED, immunizations.get(0).getMeansOfImmunization());
		assertNull(immunizations.get(0).getValidFrom());
		assertNull(immunizations.get(0).getValidUntil());
	}

	@Test
	@DisplayName("several immunizations come back separately rather than merged")
	public void severalImmunizationsStaySeparate() {

		List<EpipulseImmunizationCheckDto> immunizations =
			VaccinationFields.parseImmunizations("1|VACCINATION_RECOVERY|2024-02-01|#2|NOT_IMMUNIZED||");

		assertEquals(2, immunizations.size());
		assertEquals(MeansOfImmunization.VACCINATION_RECOVERY, immunizations.get(0).getMeansOfImmunization());
		assertEquals(MeansOfImmunization.NOT_IMMUNIZED, immunizations.get(1).getMeansOfImmunization());
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
