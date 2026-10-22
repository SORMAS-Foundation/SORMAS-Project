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
 * {@code ModeOfTransmission} mapping for MALA, read from
 * {@code epidata.modeoftransmission}.
 *
 * <p>
 * EpiPulse defines this variable per subject code, so this set is intentionally different from
 * SALM's {@link EpipulseModeOfTransmissionRef}. For malaria, key distinctions are mosquito
 * transmission contexts inside the reporting country:
 * <ul>
 * <li>{@code MOSQIMP} = imported mosquito ({@code MOSQUITOES_BY_AIR})</li>
 * <li>{@code MOSQINTRO} = local chain linked to imported case
 * ({@code MOSQUITOES_WITH_STRONG_EPI_EVIDENCE})</li>
 * <li>{@code MOSQINDIG} = local chain without such link
 * ({@code MOSQUITOES_WITHOUT_EVIDENCE})</li>
 * </ul>
 *
 * <p>
 * A bite abroad ({@code MOSQUITOES_FROM_ENDEMIC_COUNTRY}) has no MALA code and is reported via
 * other variables such as import/place fields.
 *
 * <p>
 * Synonym pairs are both accepted: MEDICAL_CARE/HEALTHCARE_ASSOCIATED -> {@code HAI}, and
 * FROM_MOTHER_TO_CHILD/MOTHER_TO_CHILD -> {@code MTCT}. {@code SOHO} maps
 * {@code TRANSFUSION_TRANSPLANT_RECIPIENT}. Unexpressible routes (for example
 * {@code UNKNOWN}, lab-acquired) map to blank rather than {@code OTH}.
 *
 * @see EpipulseModeOfTransmissionRef SALM's value set for the same variable
 */
public enum EpipulseMalaModeOfTransmissionRef {

	HAI(ModeOfTransmission.MEDICAL_CARE, ModeOfTransmission.HEALTHCARE_ASSOCIATED),
	MOSQIMP(ModeOfTransmission.MOSQUITOES_BY_AIR),
	MOSQINDIG(ModeOfTransmission.MOSQUITOES_WITHOUT_EVIDENCE),
	MOSQINTRO(ModeOfTransmission.MOSQUITOES_WITH_STRONG_EPI_EVIDENCE),
	MTCT(ModeOfTransmission.FROM_MOTHER_TO_CHILD, ModeOfTransmission.MOTHER_TO_CHILD),
	OTH(ModeOfTransmission.OTHER),
	SOHO(ModeOfTransmission.TRANSFUSION_TRANSPLANT_RECIPIENT);

	private final List<ModeOfTransmission> modesOfTransmission;

	EpipulseMalaModeOfTransmissionRef(ModeOfTransmission... modesOfTransmission) {
		this.modesOfTransmission = Collections.unmodifiableList(Arrays.asList(modesOfTransmission));
	}

	public List<ModeOfTransmission> getModesOfTransmission() {
		return modesOfTransmission;
	}

	/** The code for a recorded route, or {@code null} for one MALA cannot express. */
	public static String codeFor(ModeOfTransmission modeOfTransmission) {

		if (modeOfTransmission == null) {
			return null;
		}

		for (EpipulseMalaModeOfTransmissionRef ref : values()) {
			if (ref.modesOfTransmission.contains(modeOfTransmission)) {
				return ref.name();
			}
		}

		return null;
	}
}
