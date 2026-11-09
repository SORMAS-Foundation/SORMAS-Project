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

package de.symeda.sormas.api.epipulse.referencevalue;

import de.symeda.sormas.api.clinicalcourse.ComplianceWithTreatment;

/**
 * TUBE {@code PrevTreatmentCompletion} mapping from {@code healthconditions.compliancewithtreatment}.
 *
 * <p>
 * {@code PREVTTDEF} has no SORMAS enum constant and is never exported.
 * {@code NO_COMPLIANCE} and {@code TREATMENT_NOT_COMPLETED} both map to
 * {@code PREVTTNOCOMPL}. Unknown/not-applicable stay unmapped.
 */
public enum EpipulseTubePrevTreatmentCompletionRef {

	PREVTTCOMPL(ComplianceWithTreatment.TREATMENT_COMPLETED),
	PREVTTFAIL(ComplianceWithTreatment.TREATMENT_FAILED),
	PREVTTNOCOMPL(ComplianceWithTreatment.TREATMENT_NOT_COMPLETED, ComplianceWithTreatment.NO_COMPLIANCE);

	private final ComplianceWithTreatment[] complianceValues;

	EpipulseTubePrevTreatmentCompletionRef(ComplianceWithTreatment... complianceValues) {
		this.complianceValues = complianceValues;
	}

	/** Returns mapped EpiPulse code, otherwise {@code null}. */
	public static String codeFor(ComplianceWithTreatment compliance) {

		if (compliance == null) {
			return null;
		}

		for (EpipulseTubePrevTreatmentCompletionRef ref : values()) {
			for (ComplianceWithTreatment candidate : ref.complianceValues) {
				if (candidate == compliance) {
					return ref.name();
				}
			}
		}

		return null;
	}
}
