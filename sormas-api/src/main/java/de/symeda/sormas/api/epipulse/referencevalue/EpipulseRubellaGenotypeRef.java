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

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.GenoType;

/**
 * RUBE {@code Genotype} reference mapping.
 *
 * <p>
 * Genotype value sets are subject-code specific, so this enum is separate from measles/mumps even
 * though source values come from shared {@link GenoType}. Mapping is complete for rubella genotype
 * codes.
 *
 * <p>
 * {@code GENOTYPE_NA} and {@code GENOTYPE_UNK} are intentionally unmapped and exported as blank.
 */
public enum EpipulseRubellaGenotypeRef {

	RUBEV_1A(GenoType.GENOTYPE_1A),
	RUBEV_1B(GenoType.GENOTYPE_1B),
	RUBEV_1C(GenoType.GENOTYPE_1C),
	RUBEV_1D(GenoType.GENOTYPE_1D),
	RUBEV_1E(GenoType.GENOTYPE_1E),
	RUBEV_1F(GenoType.GENOTYPE_1F),
	RUBEV_1G(GenoType.GENOTYPE_1G),
	RUBEV_1H(GenoType.GENOTYPE_1H),
	RUBEV_1I(GenoType.GENOTYPE_1I),
	RUBEV_1J(GenoType.GENOTYPE_1J),
	RUBEV_2A(GenoType.GENOTYPE_2A),
	RUBEV_2B(GenoType.GENOTYPE_2B),
	RUBEV_2C(GenoType.GENOTYPE_2C);

	private final GenoType genoType;

	EpipulseRubellaGenotypeRef(GenoType genoType) {
		this.genoType = genoType;
	}

	public GenoType getGenoType() {
		return genoType;
	}

	/** Returns EpiPulse code for mapped genotype, otherwise {@code null}. */
	public static String codeFor(GenoType genoType) {

		if (genoType == null) {
			return null;
		}

		for (EpipulseRubellaGenotypeRef ref : values()) {
			if (ref.genoType == genoType) {
				return ref.name();
			}
		}

		return null;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
