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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.symeda.sormas.api.exposure.ModeOfTransmission;

/**
 * SALM {@code ModeOfTransmission} mapping from {@code epidata.modeoftransmission}.
 *
 * <p>
 * Value sets are subject-code specific, so this class maps only SALM's twelve codes.
 * Synonym pairs are handled explicitly (for example mother-to-child and lab-exposure spellings).
 *
 * <p>
 * MALA-only constants and {@code UNKNOWN} are intentionally unmapped. {@code OTH} is not used as a
 * stand-in for unknown.
 *
 * @see EpipulseMalaModeOfTransmissionRef MALA's value set for the same variable
 */
public enum EpipulseModeOfTransmissionRef {

	ANIMAL(ModeOfTransmission.ANIMAL_TO_HUMAN),
	FOOD(ModeOfTransmission.FOOD_OR_WATER),
	HAI(ModeOfTransmission.HEALTHCARE_ASSOCIATED),
	IDU(ModeOfTransmission.INJECTING_DRUG_USERS),
	LAB(ModeOfTransmission.LAB_OCCUPATIONAL_EXPOSURE, ModeOfTransmission.BY_LAB),
	MTCT(ModeOfTransmission.MOTHER_TO_CHILD, ModeOfTransmission.FROM_MOTHER_TO_CHILD),
	ORGAN(ModeOfTransmission.ORGAN_RECIPIENT),
	OTH(ModeOfTransmission.OTHER),
	PTP(ModeOfTransmission.PERSON_TO_PERSON),
	RECRWATER(ModeOfTransmission.RECREATIONAL_WATER),
	SEX(ModeOfTransmission.SEXUAL),
	TRANSFU(ModeOfTransmission.TRANSFUSION_RECIPIENT);

	private final List<ModeOfTransmission> modesOfTransmission;

	EpipulseModeOfTransmissionRef(ModeOfTransmission... modesOfTransmission) {
		this.modesOfTransmission = Collections.unmodifiableList(Arrays.asList(modesOfTransmission));
	}

	public List<ModeOfTransmission> getModesOfTransmission() {
		return modesOfTransmission;
	}

	/** Returns EpiPulse code for mapped route, otherwise {@code null}. */
	public static String codeFor(ModeOfTransmission modeOfTransmission) {

		if (modeOfTransmission == null) {
			return null;
		}

		for (EpipulseModeOfTransmissionRef ref : values()) {
			if (ref.modesOfTransmission.contains(modeOfTransmission)) {
				return ref.name();
			}
		}

		return null;
	}
}
