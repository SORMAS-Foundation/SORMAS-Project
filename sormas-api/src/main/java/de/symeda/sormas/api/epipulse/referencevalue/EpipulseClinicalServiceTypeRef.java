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

import de.symeda.sormas.api.epidata.TypeOfClinicalService;

/**
 * {@code ClinicalServiceType} mapping from {@code epidata.typeofclinicalservice}.
 *
 * <p>
 * Enum names are EpiPulse codes. This variable uses one shared value set across CHLAM, GONO, LGV,
 * and SYPH, with a one-to-one mapping to {@link TypeOfClinicalService}.
 *
 * <p>
 * Unrecorded service maps to blank, not {@code OTH}. {@code OTH} means a recorded service outside
 * named categories.
 */
public enum EpipulseClinicalServiceTypeRef {

	ANC(TypeOfClinicalService.ANTENATAL_CARE),
	COMB(TypeOfClinicalService.COMBINED_SERVICE),
	DV(TypeOfClinicalService.DERMATOLOGY_VENEREOLOGY_CLINIC),
	ED(TypeOfClinicalService.HOSPITAL_EMERGENCY_DEPARTMENT),
	FPC(TypeOfClinicalService.FAMILY_PLANNING_CLINIC),
	GP(TypeOfClinicalService.GENERAL_PRACTITIONER),
	GYN(TypeOfClinicalService.GYNAECOLOGY_CLINIC),
	ID(TypeOfClinicalService.INFECTIOUS_DISEASE_CLINIC),
	OPC(TypeOfClinicalService.OTHER_PRIMARY_CARE),
	OTH(TypeOfClinicalService.OTHER),
	STI(TypeOfClinicalService.DEDICATED_STI_CLINIC),
	URO(TypeOfClinicalService.UROLOGY),
	YTH(TypeOfClinicalService.YOUTH_CLINIC);

	private final TypeOfClinicalService typeOfClinicalService;

	EpipulseClinicalServiceTypeRef(TypeOfClinicalService typeOfClinicalService) {
		this.typeOfClinicalService = typeOfClinicalService;
	}

	public TypeOfClinicalService getTypeOfClinicalService() {
		return typeOfClinicalService;
	}

	/** Returns code for recorded service, or {@code null} when absent/unmapped. */
	public static String codeFor(TypeOfClinicalService typeOfClinicalService) {

		if (typeOfClinicalService == null) {
			return null;
		}

		for (EpipulseClinicalServiceTypeRef ref : values()) {
			if (ref.typeOfClinicalService == typeOfClinicalService) {
				return ref.name();
			}
		}

		return null;
	}
}
