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

import de.symeda.sormas.api.sample.Serotype;

/**
 * DENGUE {@code Serotype} mapping from {@code pathogentest.serotype}.
 *
 * <p>
 * Enum names are EpiPulse codes. {@code DENV1..DENV4} map one-to-one to dengue serotypes;
 * {@code DENVOTH} maps SORMAS {@code OTHER} ("not specified" in EpiPulse terms).
 *
 * <p>
 * {@code UNKNOWN} and non-dengue serotypes are intentionally left unmapped and exported as blank.
 */
public enum EpipulseDengueSerotypeRef {

	DENV1(Serotype.DENV_1),
	DENV2(Serotype.DENV_2),
	DENV3(Serotype.DENV_3),
	DENV4(Serotype.DENV_4),
	DENVOTH(Serotype.OTHER);

	private final Serotype serotype;

	EpipulseDengueSerotypeRef(Serotype serotype) {
		this.serotype = serotype;
	}

	public Serotype getSerotype() {
		return serotype;
	}

	/** Returns EpiPulse code for mapped serotype, otherwise {@code null}. */
	public static String codeFor(Serotype serotype) {

		if (serotype == null) {
			return null;
		}

		for (EpipulseDengueSerotypeRef ref : values()) {
			if (ref.serotype == serotype) {
				return ref.name();
			}
		}

		return null;
	}
}
