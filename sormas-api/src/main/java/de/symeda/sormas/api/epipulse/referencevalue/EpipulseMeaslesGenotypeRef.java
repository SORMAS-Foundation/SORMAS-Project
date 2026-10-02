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
 * MEAS {@code Genotype} reference mapping.
 *
 * <p>
 * {@code Genotype} lists are subject-code specific ({@code MEASV_*}, {@code MUMPV_*},
 * {@code RUBEV_*}), so this enum is measles-only even though source values come from shared
 * {@link GenoType}. Unmapped values are exported as blank.
 */
public enum EpipulseMeaslesGenotypeRef {

	MEASV_A(GenoType.GENOTYPE_A),
	/** EpiPulse code exists, but SORMAS has no corresponding {@link GenoType} constant. */
	MEASV_B1(null),
	MEASV_B2(GenoType.GENOTYPE_B2),
	MEASV_B3(GenoType.GENOTYPE_B3),
	MEASV_C1(GenoType.GENOTYPE_C1),
	MEASV_C2(GenoType.GENOTYPE_C2),
	MEASV_D1(GenoType.GENOTYPE_D1),
	MEASV_D2(GenoType.GENOTYPE_D2),
	MEASV_D3(GenoType.GENOTYPE_D3),
	MEASV_D4(GenoType.GENOTYPE_D4),
	MEASV_D5(GenoType.GENOTYPE_D5),
	MEASV_D6(GenoType.GENOTYPE_D6),
	MEASV_D7(GenoType.GENOTYPE_D7),
	MEASV_D8(GenoType.GENOTYPE_D8),
	MEASV_D9(GenoType.GENOTYPE_D9),
	MEASV_D10(GenoType.GENOTYPE_D10),
	MEASV_D11(GenoType.GENOTYPE_D11),
	MEASV_E(GenoType.GENOTYPE_E),
	MEASV_F(GenoType.GENOTYPE_F),
	MEASV_G1(GenoType.GENOTYPE_G1),
	MEASV_G2(GenoType.GENOTYPE_G2),
	MEASV_G3(GenoType.GENOTYPE_G3),
	MEASV_H1(GenoType.GENOTYPE_H1),
	MEASV_H2(GenoType.GENOTYPE_H2);

	private final GenoType genoType;

	EpipulseMeaslesGenotypeRef(GenoType genoType) {
		this.genoType = genoType;
	}

	public GenoType getGenoType() {
		return genoType;
	}

	/** Returns mapped enum value for recorded genotype, or {@code null} if unmapped. */
	public static EpipulseMeaslesGenotypeRef getByGenoType(GenoType genoType) {
		if (genoType == null) {
			return null;
		}

		for (EpipulseMeaslesGenotypeRef ref : values()) {
			if (ref.genoType == genoType) {
				return ref;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
