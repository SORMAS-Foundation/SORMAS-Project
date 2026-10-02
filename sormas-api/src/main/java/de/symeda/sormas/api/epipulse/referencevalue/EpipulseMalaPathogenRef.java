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

import de.symeda.sormas.api.sample.PathogenSpecie;

/**
 * MALA {@code Pathogen} mapping from {@code pathogentest.specie}.
 *
 * <p>
 * This class is MALA-specific: {@code Pathogen} value sets are subject-code specific, even though
 * they share {@link PathogenSpecie} as source container.
 *
 * <p>
 * Mapping is almost a rename. {@code PLASSPP} intentionally covers both {@code SPP} and
 * {@code NOT_SPECIFIED}. Unexpressible values (for example {@code COINFECTION}, {@code OTHER},
 * {@code UNKNOWN}, {@code NOT_APPLICABLE}) map to blank.
 */
public enum EpipulseMalaPathogenRef {

	PLASCYNO(PathogenSpecie.CYNOMOLGI),
	PLASFALCI(PathogenSpecie.FALCIPARUM),
	PLASKNOW(PathogenSpecie.KNOWLESI),
	PLASMALA(PathogenSpecie.MALARIAE),
	PLASOVALE(PathogenSpecie.OVALE),
	PLASSPP(PathogenSpecie.SPP, PathogenSpecie.NOT_SPECIFIED),
	PLASVIVAX(PathogenSpecie.VIVAX);

	private final List<PathogenSpecie> species;

	EpipulseMalaPathogenRef(PathogenSpecie... species) {
		this.species = Collections.unmodifiableList(Arrays.asList(species));
	}

	public List<PathogenSpecie> getSpecies() {
		return species;
	}

	/** Returns EpiPulse code for a mapped species, otherwise {@code null}. */
	public static String codeFor(PathogenSpecie specie) {

		if (specie == null) {
			return null;
		}

		for (EpipulseMalaPathogenRef ref : values()) {
			if (ref.species.contains(specie)) {
				return ref.name();
			}
		}

		return null;
	}
}
