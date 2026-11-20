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

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationFacade;
import de.symeda.sormas.api.immunization.ImmunizationValidityCalculator;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.UtilDate;

/**
 * Default validity calculator with optional disease configuration overrides.
 * <p>
 * When no disease-specific immunization validity settings are configured,
 * vaccination-based immunizations keep the legacy behavior: validity starts with
 * the entered immunization date and does not expire.
 * </p>
 */
public class DefaultImmunizationValidityCalculator implements ImmunizationValidityCalculator {

	private final DiseaseConfigurationFacade diseaseConfigurationFacade;

	public DefaultImmunizationValidityCalculator() {
		this(null);
	}

	public DefaultImmunizationValidityCalculator(DiseaseConfigurationFacade diseaseConfigurationFacade) {
		this.diseaseConfigurationFacade = diseaseConfigurationFacade;
	}

	@Override
	public Date calculateValidFrom(Disease disease, MeansOfImmunization meansOfImmunization, Date immunizationDate, Integer numberOfDoses) {
		if (meansOfImmunization == null || immunizationDate == null) {
			return null;
		}

		if (disease != null && MeansOfImmunization.isVaccination(meansOfImmunization) && hasVaccinationProtectionOverrideConfiguration(disease)) {
			if (!hasRequiredVaccinationDoses(disease, numberOfDoses)) {
				return null;
			}

			Integer startOffsetDays = diseaseConfigurationFacade.getVaccinationProtectionStartOffsetDays(disease);
			return startOffsetDays == null ? null : UtilDate.from(UtilDate.toLocalDate(immunizationDate).plusDays(startOffsetDays));
		}

		if (disease != null
			&& !MeansOfImmunization.isVaccination(meansOfImmunization)
			&& MeansOfImmunization.isRecovery(meansOfImmunization)
			&& hasRecoveryProtectionOverrideConfiguration(disease)) {
			Integer startOffsetDays = diseaseConfigurationFacade.getRecoveryProtectionStartOffsetDays(disease);
			return startOffsetDays == null ? null : UtilDate.from(UtilDate.toLocalDate(immunizationDate).plusDays(startOffsetDays));
		}

		if (!MeansOfImmunization.isVaccination(meansOfImmunization)) {
			return null;
		}

		return immunizationDate;
	}

	@Override
	public Date calculateValidUntil(Disease disease, MeansOfImmunization meansOfImmunization, Date validFrom, Integer numberOfDoses) {
		if (meansOfImmunization == null || validFrom == null) {
			return null;
		}

		if (disease != null && MeansOfImmunization.isVaccination(meansOfImmunization) && hasVaccinationProtectionOverrideConfiguration(disease)) {
			if (!hasRequiredVaccinationDoses(disease, numberOfDoses)) {
				return null;
			}

			if (diseaseConfigurationFacade.isVaccinationProtectionLifelong(disease)) {
				return UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL);
			}

			Integer durationDays = diseaseConfigurationFacade.getVaccinationProtectionDurationDays(disease);
			if (durationDays != null) {
				return UtilDate.from(UtilDate.toLocalDate(validFrom).plusDays(durationDays));
			}

			if (MeansOfImmunization.isRecovery(meansOfImmunization) && hasRecoveryProtectionOverrideConfiguration(disease)) {
				if (diseaseConfigurationFacade.isRecoveryProtectionLifelong(disease)) {
					return UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL);
				}

				Integer recoveryDurationDays = diseaseConfigurationFacade.getRecoveryProtectionDurationDays(disease);
				if (recoveryDurationDays != null) {
					return UtilDate.from(UtilDate.toLocalDate(validFrom).plusDays(recoveryDurationDays));
				}
			}

			return null;
		}

		if (disease != null
			&& !MeansOfImmunization.isVaccination(meansOfImmunization)
			&& MeansOfImmunization.isRecovery(meansOfImmunization)
			&& hasRecoveryProtectionOverrideConfiguration(disease)) {
			if (diseaseConfigurationFacade.isRecoveryProtectionLifelong(disease)) {
				return UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL);
			}

			Integer durationDays = diseaseConfigurationFacade.getRecoveryProtectionDurationDays(disease);
			if (durationDays != null) {
				return UtilDate.from(UtilDate.toLocalDate(validFrom).plusDays(durationDays));
			}

			return null;
		}

		if (!MeansOfImmunization.isVaccination(meansOfImmunization)) {
			return null;
		}

		return UtilDate.from(ImmunizationValidityCalculator.LIFELONG_VALID_UNTIL);
	}

	private boolean hasRequiredVaccinationDoses(Disease disease, Integer numberOfDoses) {
		Integer requiredDoses = diseaseConfigurationFacade.getVaccinationProtectionRequiredDoses(disease);
		return requiredDoses == null || numberOfDoses != null && numberOfDoses >= requiredDoses;
	}

	private boolean hasVaccinationProtectionOverrideConfiguration(Disease disease) {
		return diseaseConfigurationFacade != null
			&& (diseaseConfigurationFacade.getVaccinationProtectionStartOffsetDays(disease) != null
				|| diseaseConfigurationFacade.getVaccinationProtectionRequiredDoses(disease) != null
				|| diseaseConfigurationFacade.getVaccinationProtectionDurationDays(disease) != null
				|| diseaseConfigurationFacade.isVaccinationProtectionLifelong(disease));
	}

	private boolean hasRecoveryProtectionOverrideConfiguration(Disease disease) {
		return diseaseConfigurationFacade != null
			&& (diseaseConfigurationFacade.getRecoveryProtectionStartOffsetDays(disease) != null
				|| diseaseConfigurationFacade.getRecoveryProtectionDurationDays(disease) != null
				|| diseaseConfigurationFacade.isRecoveryProtectionLifelong(disease));
	}
}
