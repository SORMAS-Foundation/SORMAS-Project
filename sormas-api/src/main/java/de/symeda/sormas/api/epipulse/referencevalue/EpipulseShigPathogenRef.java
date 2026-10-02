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

import de.symeda.sormas.api.sample.PathogenSpecie;

/**
 * SHIG {@code Pathogen} mapping from {@code pathogentest.specie}.
 *
 * <p>
 * This class is SHIG-specific because {@code Pathogen} value sets differ by subject code.
 * Mapping is one-to-one for Shigella species and complete for SHIG codes.
 *
 * <p>
 * Species belonging to other genera/diseases are intentionally unmapped ({@code null}), not
 * coerced to {@code SHISPP}.
 */
public enum EpipulseShigPathogenRef {

	SHIBOY(PathogenSpecie.BOYDII),
	SHIDYS(PathogenSpecie.DYSENTERIAE),
	SHIFLE(PathogenSpecie.FLEXNERI),
	SHISON(PathogenSpecie.SONNEI),
	SHISPP(PathogenSpecie.SHIGELLA_SPP);

	private final PathogenSpecie specie;

	EpipulseShigPathogenRef(PathogenSpecie specie) {
		this.specie = specie;
	}

	public PathogenSpecie getSpecie() {
		return specie;
	}

	/** Returns EpiPulse code for mapped species, otherwise {@code null}. */
	public static String codeFor(PathogenSpecie specie) {

		if (specie == null) {
			return null;
		}

		for (EpipulseShigPathogenRef ref : values()) {
			if (ref.specie == specie) {
				return ref.name();
			}
		}

		return null;
	}
}
