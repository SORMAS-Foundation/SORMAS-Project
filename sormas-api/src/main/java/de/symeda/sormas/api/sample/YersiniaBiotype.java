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
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Yersinia biotypes used for Yersiniosis strain characterization.
 * Biotypes classify Yersinia enterocolitica strains by biochemical and virulence properties.
 */
public enum YersiniaBiotype {

	/**
	 * Biotype 1A - Non-pathogenic strains
	 */
	BIOTYPE_1A,

	/**
	 * Biotype 1B - Highly pathogenic strains
	 */
	BIOTYPE_1B,

	/**
	 * Biotype 2 - Low pathogenicity
	 */
	BIOTYPE_2,

	/**
	 * Biotype 3 - Low pathogenicity
	 */
	BIOTYPE_3,

	/**
	 * Biotype 4 - Low pathogenicity
	 */
	BIOTYPE_4,

	/**
	 * Biotype 5 - Low pathogenicity
	 */
	BIOTYPE_5,

	/**
	 * Other biotypes not covered by standard classification
	 */
	OTHER,

	/**
	 * Biotype unknown or not yet determined
	 */
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
