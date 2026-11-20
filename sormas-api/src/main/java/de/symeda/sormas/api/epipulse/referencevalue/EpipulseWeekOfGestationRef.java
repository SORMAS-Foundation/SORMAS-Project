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

import de.symeda.sormas.api.caze.Trimester;

/**
 * RUBE {@code WeekOfGestation} mapping from {@code case.trimester}.
 *
 * Only a trimester that falls inside one band is mapped.
 */
public enum EpipulseWeekOfGestationRef {

	W1_12("W1-12", Trimester.FIRST),
	W13_20("W13-20", null),
	W20_PLUS("W20+", Trimester.THIRD);

	private final String code;
	private final Trimester trimester;

	EpipulseWeekOfGestationRef(String code, Trimester trimester) {
		this.code = code;
		this.trimester = trimester;
	}

	public String getCode() {
		return code;
	}

	public Trimester getTrimester() {
		return trimester;
	}

	/** Returns EpiPulse code for mapped trimester, otherwise {@code null}. */
	public static String codeFor(Trimester trimester) {

		if (trimester == null) {
			return null;
		}

		for (EpipulseWeekOfGestationRef ref : values()) {
			if (ref.trimester == trimester) {
				return ref.code;
			}
		}

		return null;
	}
}
