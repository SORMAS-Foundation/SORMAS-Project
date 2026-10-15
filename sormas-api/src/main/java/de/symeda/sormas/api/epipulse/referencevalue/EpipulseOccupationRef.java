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

import org.apache.commons.lang3.StringUtils;

/**
 * MALA {@code Occupation} mapping.
 *
 * <p>
 * Source is a stored string (customizable enum), so mapping is string-based by design.
 * {@code WORK_IN_AIRPORT} -> {@code AIRW}, {@code HEALTHCARE_WORKER} -> {@code HCW}; other stated
 * occupations fall back to {@code OTH}.
 *
 * <p>
 * Blank, {@code UNKNOWN}, and {@code NOT_APPLICABLE} map to blank (not {@code OTH}).
 */
public enum EpipulseOccupationRef {

	AIRW("WORK_IN_AIRPORT"),
	HCW("HEALTHCARE_WORKER"),
	OTH(null);

	/** Values indicating no stated occupation; exported as blank. */
	private static final String UNKNOWN = "UNKNOWN";
	private static final String NOT_APPLICABLE = "NOT_APPLICABLE";

	private final String occupationType;

	EpipulseOccupationRef(String occupationType) {
		this.occupationType = occupationType;
	}

	/** Stored occupation value this code maps from, or {@code null} for {@link #OTH}. */
	public String getOccupationType() {
		return occupationType;
	}

	/**
	 * Returns code for recorded occupation.
	 * Known named values map to {@code AIRW}/{@code HCW}; other stated values map to {@code OTH}.
	 */
	public static String codeFor(String occupationType) {

		if (StringUtils.isBlank(occupationType) || UNKNOWN.equals(occupationType) || NOT_APPLICABLE.equals(occupationType)) {
			return null;
		}

		for (EpipulseOccupationRef ref : values()) {
			if (occupationType.equals(ref.occupationType)) {
				return ref.name();
			}
		}

		return OTH.name();
	}
}
