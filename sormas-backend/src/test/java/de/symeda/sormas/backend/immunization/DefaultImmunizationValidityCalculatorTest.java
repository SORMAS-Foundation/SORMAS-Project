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

class DefaultImmunizationValidityCalculatorTest {

	private final DefaultImmunizationValidityCalculator cut = new DefaultImmunizationValidityCalculator();

	@Test
	void shouldUseImmunizationDateAsValidFromForVaccinationBasedMeans() {
		Date immunizationDate = UtilDate.from(LocalDate.of(2026, 1, 1));

		assertEquals(immunizationDate, cut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION, immunizationDate, 1));
		assertEquals(immunizationDate, cut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION_RECOVERY, immunizationDate, 1));
	}

	@Test
	void shouldReturnLifelongValidityForVaccinationBasedMeans() {
		Date validFrom = UtilDate.from(LocalDate.of(2026, 1, 1));

		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			cut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.VACCINATION, validFrom, 1));
	}

	@Test
	void shouldReturnNullForNonVaccinationMeansOrMissingDate() {
		assertNull(cut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.RECOVERY, UtilDate.from(LocalDate.of(2026, 1, 1)), 1));
		assertNull(cut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION, null, 1));

		assertNull(cut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.RECOVERY, UtilDate.from(LocalDate.of(2026, 1, 1)), 1));
		assertNull(cut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.VACCINATION, null, 1));
	}
}
