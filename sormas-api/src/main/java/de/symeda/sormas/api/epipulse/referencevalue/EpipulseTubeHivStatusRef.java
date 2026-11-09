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

import de.symeda.sormas.api.utils.YesNoUnknown;

/**
 * TUBE {@code HIVStatus} mapping from {@code healthconditions.hiv}.
 *
 * <p>
 * Separate from STI mapping ({@link EpipulseStiHivStatusRef}): TUBE reads
 * {@code healthconditions.hiv} and only exports {@code NEG}/{@code POS}. Unknown/unanswered values
 * remain unmapped.
 */
public enum EpipulseTubeHivStatusRef {

	NEG(YesNoUnknown.NO),
	POS(YesNoUnknown.YES);

	private final YesNoUnknown hiv;

	EpipulseTubeHivStatusRef(YesNoUnknown hiv) {
		this.hiv = hiv;
	}

	/** Returns mapped EpiPulse code, otherwise {@code null}. */
	public static String codeFor(YesNoUnknown hiv) {

		if (hiv == null) {
			return null;
		}

		for (EpipulseTubeHivStatusRef ref : values()) {
			if (ref.hiv == hiv) {
				return ref.name();
			}
		}

		return null;
	}
}
