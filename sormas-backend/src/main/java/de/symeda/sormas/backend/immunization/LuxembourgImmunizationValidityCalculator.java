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

/**
 * Calculates immunization validity periods for Luxembourg.
 * <p>
 * This calculator implements Luxembourg-specific rules for determining when immunization
 * provides protection (validFrom) and how long that protection lasts (validUntil).
 * The rules vary by disease, vaccination schedule, and number of administered doses.
 * </p>
 * <h3>Basic Concepts</h3>
 * <ul>
 * <li><strong>validFrom:</strong> The date when immunization becomes protective</li>
 * <li><strong>validUntil:</strong> The date when protection expires (or is lifelong)</li>
 * </ul>
 * <h3>Calculation Rules</h3>
 * <ul>
 * <li><strong>vaccinationStartOffsetDays:</strong> Days after vaccination until protection begins</li>
 * <li><strong>requiredVaccinationDoses:</strong> Minimum number of doses for full protection</li>
 * <li><strong>vaccinationDurationDays:</strong> How long protection lasts from vaccination</li>
 * <li><strong>recoveryDurationDays:</strong> How long protection lasts from natural infection/recovery</li>
 * </ul>
 * <p>
 * A profile with null values means: protection date unknown (null) or lifelong protection (forever).
 * </p>
 */
package de.symeda.sormas.backend.immunization;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.immunization.ImmunizationValidityCalculator;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.UtilDate;

/**
 * Calculates immunization validity periods for Luxembourg.
 * <p>
 * This calculator implements Luxembourg-specific rules for determining when immunization
 * provides protection (validFrom) and how long that protection lasts (validUntil).
 * The rules vary by disease, vaccination schedule, and number of administered doses.
 * </p>
 * <h3>Basic Concepts</h3>
 * <ul>
 * <li><strong>validFrom:</strong> The date when immunization becomes protective</li>
 * <li><strong>validUntil:</strong> The date when protection expires (or is lifelong)</li>
 * </ul>
 * <h3>Calculation Rules</h3>
 * <ul>
 * <li><strong>vaccinationStartOffsetDays:</strong> Days after vaccination until protection begins</li>
 * <li><strong>requiredVaccinationDoses:</strong> Minimum number of doses for full protection</li>
 * <li><strong>vaccinationDurationDays:</strong> How long protection lasts from vaccination</li>
 * <li><strong>recoveryDurationDays:</strong> How long protection lasts from natural infection/recovery</li>
 * </ul>
 * <p>
 * A profile with null values means: protection date unknown (null) or lifelong protection (forever).
 * </p>
 */
public class LuxembourgImmunizationValidityCalculator implements ImmunizationValidityCalculator {

	/**
	 * Sentinel value representing lifelong duration.
	 * Any duration equal to this value results in lifelong protection.
	 */
	private static final long LIFELONG_DURATION_DAYS = Long.MAX_VALUE;

	/**
	 * Map of disease-specific immunity profiles.
	 * Each profile contains parameters for calculating validFrom/validUntil periods.
	 */
	private static final Map<Disease, DiseaseImmunityProfile> DISEASE_IMMUNITY_PROFILES = buildProfiles();

	@Override
	public Date calculateValidFrom(Disease disease, MeansOfImmunization meansOfImmunization, Date immunizationDate, Integer numberOfDoses) {
		if (disease == null || immunizationDate == null || !MeansOfImmunization.isVaccination(meansOfImmunization)) {
			return null;
		}

		DiseaseImmunityProfile profile = DISEASE_IMMUNITY_PROFILES.get(disease);
		if (profile == null || profile.vaccinationStartOffsetDays == null || !profile.hasRequiredDoses(numberOfDoses)) {
			return null;
		}

		return UtilDate.from(UtilDate.toLocalDate(immunizationDate).plusDays(profile.vaccinationStartOffsetDays));
	}

	@Override
	public Date calculateValidUntil(Disease disease, MeansOfImmunization meansOfImmunization, Date validFrom, Integer numberOfDoses) {
		if (disease == null || validFrom == null || meansOfImmunization == null) {
			return null;
		}

		DiseaseImmunityProfile profile = DISEASE_IMMUNITY_PROFILES.get(disease);
		if (profile == null) {
			return null;
		}

		Long durationDays = null;
		if (MeansOfImmunization.isVaccination(meansOfImmunization) && profile.hasRequiredDoses(numberOfDoses)) {
			durationDays = profile.vaccinationDurationDays;
		}
		if (durationDays == null && MeansOfImmunization.isRecovery(meansOfImmunization)) {
			durationDays = profile.recoveryDurationDays;
		}

		if (durationDays == null) {
			return null;
		}
		if (durationDays == LIFELONG_DURATION_DAYS) {
			return UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL);
		}

		return UtilDate.from(UtilDate.toLocalDate(validFrom).plusDays(durationDays));
	}

	private static Map<Disease, DiseaseImmunityProfile> buildProfiles() {
		Map<Disease, DiseaseImmunityProfile> profiles = new EnumMap<>(Disease.class);

		profiles.put(Disease.CORONAVIRUS, DiseaseImmunityProfile.ofVaccination(14, 1, null));
		profiles.put(Disease.DENGUE, DiseaseImmunityProfile.ofVaccinationAndRecovery(28, 3, 7L * 365L, LIFELONG_DURATION_DAYS));
		profiles.put(Disease.DIPHTERIA, DiseaseImmunityProfile.ofVaccination(28, 3, 10L * 365L));
		profiles.put(Disease.INFLUENZA, DiseaseImmunityProfile.ofVaccination(14, 1, 300L));
		profiles.put(Disease.INVASIVE_MENINGOCOCCAL_INFECTION, DiseaseImmunityProfile.ofVaccination(14, 1, 5L * 365L));
		profiles.put(Disease.INVASIVE_PNEUMOCOCCAL_INFECTION, DiseaseImmunityProfile.ofVaccination(null, 1, null));
		profiles.put(Disease.MALARIA, DiseaseImmunityProfile.ofVaccination(28, 1, null));
		profiles.put(Disease.MEASLES, DiseaseImmunityProfile.ofVaccinationAndRecovery(14, 2, LIFELONG_DURATION_DAYS, LIFELONG_DURATION_DAYS));
		profiles.put(Disease.PERTUSSIS, DiseaseImmunityProfile.ofVaccination(14, 1, 10L * 365L));
		profiles.put(Disease.POLIO, DiseaseImmunityProfile.ofVaccination(14, 1, null));
		profiles.put(Disease.RABIES, DiseaseImmunityProfile.ofVaccination(14, 1, null));
		profiles.put(Disease.RUBELLA, DiseaseImmunityProfile.ofVaccinationAndRecovery(14, 2, LIFELONG_DURATION_DAYS, LIFELONG_DURATION_DAYS));
		profiles.put(Disease.NON_NEONATAL_TETANUS, DiseaseImmunityProfile.ofVaccination(14, 1, 10L * 365L));
		profiles.put(Disease.NEONATAL_TETANUS, DiseaseImmunityProfile.ofVaccination(14, 1, 10L * 365L));
		profiles.put(Disease.TUBERCULOSIS, DiseaseImmunityProfile.ofVaccination(null, 1, null));
		profiles.put(Disease.YELLOW_FEVER, DiseaseImmunityProfile.ofVaccinationAndRecovery(10, 1, LIFELONG_DURATION_DAYS, LIFELONG_DURATION_DAYS));

		return profiles;
	}

	private static final class DiseaseImmunityProfile {

		private final Integer vaccinationStartOffsetDays;
		private final Integer requiredVaccinationDoses;
		private final Long vaccinationDurationDays;
		private final Long recoveryDurationDays;

		private DiseaseImmunityProfile(
			Integer vaccinationStartOffsetDays,
			Integer requiredVaccinationDoses,
			Long vaccinationDurationDays,
			Long recoveryDurationDays) {

			this.vaccinationStartOffsetDays = vaccinationStartOffsetDays;
			this.requiredVaccinationDoses = requiredVaccinationDoses;
			this.vaccinationDurationDays = vaccinationDurationDays;
			this.recoveryDurationDays = recoveryDurationDays;
		}

		private static DiseaseImmunityProfile ofVaccination(
			Integer vaccinationStartOffsetDays,
			Integer requiredVaccinationDoses,
			Long vaccinationDurationDays) {
			return new DiseaseImmunityProfile(vaccinationStartOffsetDays, requiredVaccinationDoses, vaccinationDurationDays, null);
		}

		private static DiseaseImmunityProfile ofVaccinationAndRecovery(
			Integer vaccinationStartOffsetDays,
			Integer requiredVaccinationDoses,
			Long vaccinationDurationDays,
			Long recoveryDurationDays) {

			return new DiseaseImmunityProfile(vaccinationStartOffsetDays, requiredVaccinationDoses, vaccinationDurationDays, recoveryDurationDays);
		}

		private boolean hasRequiredDoses(Integer numberOfDoses) {
			return requiredVaccinationDoses == null || numberOfDoses != null && numberOfDoses >= requiredVaccinationDoses;
		}
	}
}
