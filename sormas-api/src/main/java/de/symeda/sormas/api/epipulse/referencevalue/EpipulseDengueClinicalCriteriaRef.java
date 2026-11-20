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

import de.symeda.sormas.api.symptoms.ClinicalManifestation;

/**
 * DENGUE {@code ClinicalCriteria} mapping from {@code symptoms.clinicalManifestation}.
 *
 * <p>
 * {@code UNKNOWN} and {@code OTHER} are intentionally left unmapped and exported as blank.
 */
public enum EpipulseDengueClinicalCriteriaRef {

	ASY(ClinicalManifestation.ASYMPTOMATIC),
	NSEV(ClinicalManifestation.NO_SIGN_OF_SEVERITY),
	SEV(ClinicalManifestation.SIGN_OF_SEVERITY);

	private final ClinicalManifestation clinicalManifestation;

	EpipulseDengueClinicalCriteriaRef(ClinicalManifestation clinicalManifestation) {
		this.clinicalManifestation = clinicalManifestation;
	}

	public ClinicalManifestation getClinicalManifestation() {
		return clinicalManifestation;
	}

	/** Returns EpiPulse code for mapped clinical manifestation, otherwise {@code null}. */
	public static String codeFor(ClinicalManifestation clinicalManifestation) {

		if (clinicalManifestation == null) {
			return null;
		}

		for (EpipulseDengueClinicalCriteriaRef ref : values()) {
			if (ref.clinicalManifestation == clinicalManifestation) {
				return ref.name();
			}
		}

		return null;
	}
}
