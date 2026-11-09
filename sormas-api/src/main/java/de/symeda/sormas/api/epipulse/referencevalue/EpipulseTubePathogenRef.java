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
 * TUBE {@code Pathogen} mapping from {@code pathogentest.specie}.
 *
 * <p>
 * TUBE-specific mapping. SORMAS has no dedicated {@code MCAPRAE} species constant, so caprae-like
 * cases can only be represented as {@link PathogenSpecie#OTHER_MTBC_MEMBER} and export as
 * {@code MTUBECOMPOTH}. Non-MTBC genera stay unmapped.
 */
public enum EpipulseTubePathogenRef {

	MAFRICA(PathogenSpecie.MYCOBATERIUM_AFRICANUM),
	MBOVIS(PathogenSpecie.MYCOBATERIUM_BOVIS),
	MTUBE(PathogenSpecie.MYCOBATERIUM_TUBERCULOSIS),
	MTUBECOMPOTH(PathogenSpecie.OTHER_MTBC_MEMBER);

	private final PathogenSpecie specie;

	EpipulseTubePathogenRef(PathogenSpecie specie) {
		this.specie = specie;
	}

	public PathogenSpecie getSpecie() {
		return specie;
	}

	/** Returns mapped EpiPulse code, otherwise {@code null}. */
	public static String codeFor(PathogenSpecie specie) {

		if (specie == null) {
			return null;
		}

		for (EpipulseTubePathogenRef ref : values()) {
			if (ref.specie == specie) {
				return ref.name();
			}
		}

		return null;
	}
}
