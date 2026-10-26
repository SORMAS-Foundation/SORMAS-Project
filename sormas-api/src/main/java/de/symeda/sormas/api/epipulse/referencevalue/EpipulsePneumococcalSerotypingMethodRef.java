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
import de.symeda.sormas.api.sample.SerotypingMethod;

/**
 * PNEU-specific {@code PathogenDetectionMethod} mapping from
 * {@link SerotypingMethod}.
 *
 * <p>
 * For PNEU this variable means serotyping technique, not generic detection test type, so it is
 * separate from {@link EpipulsePathogenTestTypeRef}.
 *
 * <p>
 * {@link #resolve(SerotypingMethod)} falls back to {@code OTH} for recorded but unlisted methods;
 * null input remains null.
 */
public enum EpipulsePneumococcalSerotypingMethodRef {

	COAGG(SerotypingMethod.COAGGLUTINATION),
	GDIFF(SerotypingMethod.GEL_DIFFUSION),
	MPCR(SerotypingMethod.MULTIPLEX_PCR),
	PTEST(SerotypingMethod.PNEUMOTEST),
	QUE(SerotypingMethod.QUELLUNG_REACTION),
	SLAGG(SerotypingMethod.SLIDE_AGGLUTINATION),
	OTH(SerotypingMethod.OTHER);

	private final SerotypingMethod serotypingMethod;

	EpipulsePneumococcalSerotypingMethodRef(SerotypingMethod serotypingMethod) {
		this.serotypingMethod = serotypingMethod;
	}

	public SerotypingMethod getSerotypingMethod() {
		return serotypingMethod;
	}

	/**
	 * Pure lookup: returns listed code or {@code null}.
	 */
	public static String codeFor(SerotypingMethod serotypingMethod) {

		if (serotypingMethod == null) {
			return null;
		}

		for (EpipulsePneumococcalSerotypingMethodRef ref : values()) {
			if (ref.serotypingMethod == serotypingMethod) {
				return ref.name();
			}
		}

		return null;
	}

	/**
	 * Returns reportable code for recorded method: listed code, otherwise {@code OTH}; null stays
	 * null.
	 */
	public static String resolve(SerotypingMethod serotypingMethod) {

		if (serotypingMethod == null) {
			return null;
		}

		String code = codeFor(serotypingMethod);

		return code != null ? code : OTH.name();
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
