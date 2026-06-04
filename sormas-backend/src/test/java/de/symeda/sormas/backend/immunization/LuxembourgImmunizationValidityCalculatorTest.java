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

package de.symeda.sormas.backend.immunization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationValidityCalculator;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.UtilDate;

class LuxembourgImmunizationValidityCalculatorTest {

	private final LuxembourgImmunizationValidityCalculator cut = new LuxembourgImmunizationValidityCalculator();

	@Test
	void testCalculateValidFromForVaccinationProfiles() {
		Date baseDate = UtilDate.from(LocalDate.of(2026, 1, 1));

		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 15)),
			cut.calculateValidFrom(Disease.CORONAVIRUS, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertNull(cut.calculateValidFrom(Disease.DENGUE, MeansOfImmunization.VACCINATION, baseDate, 2));
		assertEquals(UtilDate.from(LocalDate.of(2026, 1, 29)), cut.calculateValidFrom(Disease.DENGUE, MeansOfImmunization.VACCINATION, baseDate, 3));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 29)),
			cut.calculateValidFrom(Disease.DIPHTERIA, MeansOfImmunization.VACCINATION, baseDate, 3));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 15)),
			cut.calculateValidFrom(Disease.INFLUENZA, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 15)),
			cut.calculateValidFrom(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertNull(cut.calculateValidFrom(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(UtilDate.from(LocalDate.of(2026, 1, 29)), cut.calculateValidFrom(Disease.MALARIA, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertNull(cut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(UtilDate.from(LocalDate.of(2026, 1, 15)), cut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION, baseDate, 2));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 15)),
			cut.calculateValidFrom(Disease.PERTUSSIS, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(UtilDate.from(LocalDate.of(2026, 1, 15)), cut.calculateValidFrom(Disease.POLIO, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(UtilDate.from(LocalDate.of(2026, 1, 15)), cut.calculateValidFrom(Disease.RABIES, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertNull(cut.calculateValidFrom(Disease.RUBELLA, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(UtilDate.from(LocalDate.of(2026, 1, 15)), cut.calculateValidFrom(Disease.RUBELLA, MeansOfImmunization.VACCINATION, baseDate, 2));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 15)),
			cut.calculateValidFrom(Disease.NON_NEONATAL_TETANUS, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertNull(cut.calculateValidFrom(Disease.TUBERCULOSIS, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 11)),
			cut.calculateValidFrom(Disease.YELLOW_FEVER, MeansOfImmunization.VACCINATION, baseDate, 1));
	}

	@Test
	void testCalculateValidUntilForVaccinationAndRecoveryProfiles() {
		Date validFrom = UtilDate.from(LocalDate.of(2026, 2, 1));

		assertNull(cut.calculateValidUntil(Disease.CORONAVIRUS, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertEquals(
			UtilDate.from(LocalDate.of(2033, 1, 30)),
			cut.calculateValidUntil(Disease.DENGUE, MeansOfImmunization.VACCINATION, validFrom, 3));
		assertEquals(
			UtilDate.from(LocalDate.of(2036, 1, 30)),
			cut.calculateValidUntil(Disease.DIPHTERIA, MeansOfImmunization.VACCINATION, validFrom, 3));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 11, 28)),
			cut.calculateValidUntil(Disease.INFLUENZA, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertEquals(
			UtilDate.from(LocalDate.of(2031, 1, 31)),
			cut.calculateValidUntil(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertNull(cut.calculateValidUntil(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertNull(cut.calculateValidUntil(Disease.MALARIA, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			cut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.VACCINATION, validFrom, 2));
		assertEquals(
			UtilDate.from(LocalDate.of(2036, 1, 30)),
			cut.calculateValidUntil(Disease.PERTUSSIS, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertNull(cut.calculateValidUntil(Disease.POLIO, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertNull(cut.calculateValidUntil(Disease.RABIES, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			cut.calculateValidUntil(Disease.RUBELLA, MeansOfImmunization.VACCINATION, validFrom, 2));
		assertEquals(
			UtilDate.from(LocalDate.of(2036, 1, 30)),
			cut.calculateValidUntil(Disease.NON_NEONATAL_TETANUS, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertNull(cut.calculateValidUntil(Disease.TUBERCULOSIS, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			cut.calculateValidUntil(Disease.YELLOW_FEVER, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			cut.calculateValidUntil(Disease.DENGUE, MeansOfImmunization.RECOVERY, validFrom, null));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			cut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.RECOVERY, validFrom, null));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			cut.calculateValidUntil(Disease.RUBELLA, MeansOfImmunization.RECOVERY, validFrom, null));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			cut.calculateValidUntil(Disease.YELLOW_FEVER, MeansOfImmunization.RECOVERY, validFrom, null));
		assertNull(cut.calculateValidUntil(Disease.INFLUENZA, MeansOfImmunization.RECOVERY, validFrom, null));
		assertNull(cut.calculateValidUntil(Disease.CORONAVIRUS, MeansOfImmunization.RECOVERY, validFrom, null));
	}
}
