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
 * {@code ModeOfTransmission} mapping for DENGUE from
 * {@code epidata.modeoftransmission}.
 *
 * <p>
 * DENGUE has its own short value set (4 codes), distinct from SALM and MALA.
 * {@code MOSQ} intentionally collapses all SORMAS mosquito-route variants, because dengue does not
 * split mosquito transmission by context here.
 *
 * <p>
 * {@code SOHO} covers transfusion/transplant/reproductive-cell acquisition and maps all three
 * SORMAS spellings. {@code UNKNOWN} and other unexpressible routes map to blank (not
 * {@code OTH}).
 *
 * <p>
 * Case-form availability is separate from this mapping: if dengue cannot currently record a given
 * source value, this mapper still defines the conversion rule.
 *
 * @see EpipulseModeOfTransmissionRef SALM's value set for the same variable
 * @see EpipulseMalaModeOfTransmissionRef MALA's value set for the same variable
 */
public enum EpipulseDengueModeOfTransmissionRef {

	MOSQ(ModeOfTransmission.MOSQUITOES_FROM_ENDEMIC_COUNTRY,
		ModeOfTransmission.MOSQUITOES_BY_AIR,
		ModeOfTransmission.MOSQUITOES_WITH_STRONG_EPI_EVIDENCE,
		ModeOfTransmission.MOSQUITOES_WITHOUT_EVIDENCE),
	OTH(ModeOfTransmission.OTHER),
	SEX(ModeOfTransmission.SEXUAL),
	SOHO(ModeOfTransmission.TRANSFUSION_TRANSPLANT_RECIPIENT, ModeOfTransmission.TRANSFUSION_RECIPIENT, ModeOfTransmission.ORGAN_RECIPIENT);

	private final List<ModeOfTransmission> modesOfTransmission;

	EpipulseDengueModeOfTransmissionRef(ModeOfTransmission... modesOfTransmission) {
		this.modesOfTransmission = Collections.unmodifiableList(Arrays.asList(modesOfTransmission));
	}

	public List<ModeOfTransmission> getModesOfTransmission() {
		return modesOfTransmission;
	}

	/** The code for a recorded route, or {@code null} for one DENGUE cannot express. */
	public static String codeFor(ModeOfTransmission modeOfTransmission) {

		if (modeOfTransmission == null) {
			return null;
		}

		for (EpipulseDengueModeOfTransmissionRef ref : values()) {
			if (ref.modesOfTransmission.contains(modeOfTransmission)) {
				return ref.name();
			}
		}

		return null;
	}
}
