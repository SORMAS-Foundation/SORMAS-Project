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
import static org.mockito.Mockito.lenient;

import java.time.LocalDate;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.api.immunization.ImmunizationValidityCalculator;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.backend.AbstractUnitTest;

class DefaultImmunizationValidityCalculatorTest extends AbstractUnitTest {

	private final DefaultImmunizationValidityCalculator legacyCut = new DefaultImmunizationValidityCalculator();

	@Mock
	private DiseaseConfigurationFacade diseaseConfigurationFacade;

	private DefaultImmunizationValidityCalculator configuredCut;

	private void configureVaccination(Disease disease, Integer startOffsetDays, Integer requiredDoses, Integer durationDays, boolean lifelong) {
		lenient().when(diseaseConfigurationFacade.getVaccinationProtectionStartOffsetDays(disease)).thenReturn(startOffsetDays);
		lenient().when(diseaseConfigurationFacade.getVaccinationProtectionRequiredDoses(disease)).thenReturn(requiredDoses);
		lenient().when(diseaseConfigurationFacade.getVaccinationProtectionDurationDays(disease)).thenReturn(durationDays);
		lenient().when(diseaseConfigurationFacade.isVaccinationProtectionLifelong(disease)).thenReturn(lifelong);
	}

	private void configureRecovery(Disease disease, Integer startOffsetDays, Integer durationDays, boolean lifelong) {
		lenient().when(diseaseConfigurationFacade.getRecoveryProtectionStartOffsetDays(disease)).thenReturn(startOffsetDays);
		lenient().when(diseaseConfigurationFacade.getRecoveryProtectionDurationDays(disease)).thenReturn(durationDays);
		lenient().when(diseaseConfigurationFacade.isRecoveryProtectionLifelong(disease)).thenReturn(lifelong);
	}

	private void configureProfiles() {
		configuredCut = new DefaultImmunizationValidityCalculator(diseaseConfigurationFacade);

		configureVaccination(Disease.CORONAVIRUS, 14, 3, null, false);
		configureRecovery(Disease.CORONAVIRUS, null, null, false);

		configureVaccination(Disease.DENGUE, 28, 3, 7 * 365, false);
		configureRecovery(Disease.DENGUE, 0, null, true);

		configureVaccination(Disease.DIPHTHERIA, 28, 3, 10 * 365, false);
		configureRecovery(Disease.DIPHTHERIA, null, null, false);

		configureVaccination(Disease.INFLUENZA, 14, 1, 300, false);
		configureRecovery(Disease.INFLUENZA, null, null, false);

		configureVaccination(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, 14, 3, null, false);
		configureRecovery(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, null, null, false);

		configureVaccination(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, null, 2, null, false);
		configureRecovery(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, null, null, false);

		configureVaccination(Disease.MALARIA, 28, 3, null, false);
		configureRecovery(Disease.MALARIA, null, null, false);

		configureVaccination(Disease.MEASLES, 14, 2, null, true);
		configureRecovery(Disease.MEASLES, 0, null, true);

		configureVaccination(Disease.PERTUSSIS, 14, 3, 10 * 365, false);
		configureRecovery(Disease.PERTUSSIS, null, null, false);

		configureVaccination(Disease.POLIO, 14, 3, null, false);
		configureRecovery(Disease.POLIO, null, null, false);

		configureVaccination(Disease.RABIES, 14, 2, null, false);
		configureRecovery(Disease.RABIES, null, null, false);

		configureVaccination(Disease.RUBELLA, 14, 2, null, true);
		configureRecovery(Disease.RUBELLA, 0, null, true);

		configureVaccination(Disease.NON_NEONATAL_TETANUS, 14, 3, 10 * 365, false);
		configureRecovery(Disease.NON_NEONATAL_TETANUS, null, null, false);

		configureVaccination(Disease.TUBERCULOSIS, null, 1, null, false);
		configureRecovery(Disease.TUBERCULOSIS, null, null, false);

		configureVaccination(Disease.YELLOW_FEVER, 10, 1, null, true);
		configureRecovery(Disease.YELLOW_FEVER, 0, null, true);
	}

	@Test
	void shouldUseImmunizationDateAsValidFromForVaccinationBasedMeans() {
		Date immunizationDate = UtilDate.from(LocalDate.of(2026, 1, 1));

		assertEquals(immunizationDate, legacyCut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION, immunizationDate, 1));
		assertEquals(immunizationDate, legacyCut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION_RECOVERY, immunizationDate, 1));
	}

	@Test
	void shouldReturnLifelongValidityForVaccinationBasedMeans() {
		Date validFrom = UtilDate.from(LocalDate.of(2026, 1, 1));

		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			legacyCut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.VACCINATION, validFrom, 1));
	}

	@Test
	void shouldReturnNullForNonVaccinationMeansOrMissingDate() {
		assertNull(legacyCut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.RECOVERY, UtilDate.from(LocalDate.of(2026, 1, 1)), 1));
		assertNull(legacyCut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION, null, 1));

		assertNull(legacyCut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.RECOVERY, UtilDate.from(LocalDate.of(2026, 1, 1)), 1));
		assertNull(legacyCut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.VACCINATION, null, 1));
	}

	@Test
	void shouldUseDiseaseConfigurationForConfiguredVaccinationRules() {
		configureProfiles();
		Date baseDate = UtilDate.from(LocalDate.of(2026, 1, 1));

		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 15)),
			configuredCut.calculateValidFrom(Disease.CORONAVIRUS, MeansOfImmunization.VACCINATION, baseDate, 3));
		assertNull(configuredCut.calculateValidFrom(Disease.CORONAVIRUS, MeansOfImmunization.VACCINATION, baseDate, 2));
		assertNull(configuredCut.calculateValidFrom(Disease.DENGUE, MeansOfImmunization.VACCINATION, baseDate, 2));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 29)),
			configuredCut.calculateValidFrom(Disease.DENGUE, MeansOfImmunization.VACCINATION, baseDate, 3));
		assertNull(configuredCut.calculateValidFrom(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 15)),
			configuredCut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION, baseDate, 2));
		assertNull(configuredCut.calculateValidFrom(Disease.MEASLES, MeansOfImmunization.VACCINATION, baseDate, 1));
		assertEquals(
			UtilDate.from(LocalDate.of(2026, 1, 11)),
			configuredCut.calculateValidFrom(Disease.YELLOW_FEVER, MeansOfImmunization.VACCINATION, baseDate, 1));
	}

	@Test
	void shouldUseDiseaseConfigurationForConfiguredValidityEndRules() {
		configureProfiles();
		Date validFrom = UtilDate.from(LocalDate.of(2026, 2, 1));

		assertNull(configuredCut.calculateValidUntil(Disease.CORONAVIRUS, MeansOfImmunization.VACCINATION, validFrom, 3));
		assertEquals(
			UtilDate.from(LocalDate.of(2033, 1, 30)),
			configuredCut.calculateValidUntil(Disease.DENGUE, MeansOfImmunization.VACCINATION, validFrom, 3));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			configuredCut.calculateValidUntil(Disease.MEASLES, MeansOfImmunization.VACCINATION, validFrom, 2));
		assertEquals(
			UtilDate.from(LocalDate.of(2036, 1, 30)),
			configuredCut.calculateValidUntil(Disease.NON_NEONATAL_TETANUS, MeansOfImmunization.VACCINATION, validFrom, 3));
		assertNull(configuredCut.calculateValidUntil(Disease.TUBERCULOSIS, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			configuredCut.calculateValidUntil(Disease.YELLOW_FEVER, MeansOfImmunization.VACCINATION, validFrom, 1));
		assertEquals(
			UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL),
			configuredCut.calculateValidUntil(Disease.DENGUE, MeansOfImmunization.RECOVERY, validFrom, null));
		assertNull(configuredCut.calculateValidUntil(Disease.INFLUENZA, MeansOfImmunization.RECOVERY, validFrom, null));
	}
}
