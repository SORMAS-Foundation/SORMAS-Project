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

import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectiousness;

/**
 * {@code StageSYPH} coarse syphilis stage mapping from
 * {@code symptoms.syphilisinfectiousness}.
 *
 * <p>
 * This value is reported independently from detailed stage
 * ({@link EpipulseStageSyphDetailedRef}). {@code UNKNOWN} is exported as blank.
 */
public enum EpipulseStageSyphRef {

	I(SyphilisInfectiousness.INFECTIOUS),
	NI(SyphilisInfectiousness.NOT_INFECTIOUS);

	private final SyphilisInfectiousness infectiousness;

	EpipulseStageSyphRef(SyphilisInfectiousness infectiousness) {
		this.infectiousness = infectiousness;
	}

	public SyphilisInfectiousness getInfectiousness() {
		return infectiousness;
	}

	/** Returns EpiPulse code for mapped infectiousness, otherwise {@code null}. */
	public static String codeFor(SyphilisInfectiousness infectiousness) {

		if (infectiousness == null) {
			return null;
		}

		for (EpipulseStageSyphRef ref : values()) {
			if (ref.infectiousness == infectiousness) {
				return ref.name();
			}
		}

		return null;
	}
}
