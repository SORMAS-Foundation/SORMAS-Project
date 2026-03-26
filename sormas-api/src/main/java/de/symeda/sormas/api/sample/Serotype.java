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

import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

/**
 * Serotypes of disease.
 */

public enum Serotype {

	@Diseases({
		Disease.DENGUE })
	DENV_1,
	@Diseases({
		Disease.DENGUE })
	DENV_2,
	@Diseases({
		Disease.DENGUE })
	DENV_3,
	@Diseases({
		Disease.DENGUE })
	DENV_4,
	OTHER;

	// if a serotype is not in the list (existing serotypes) -> will be displayed with OTHER along with its value in the serotypetext.
	public static Serotype fromString(String serotype) {
		if (StringUtils.isBlank(serotype)) {
			return null;
		}
		String serotypeEnumVal = StringUtils.trim(serotype).toUpperCase(Locale.ROOT).replaceAll("[-_\\s]+", "_");

		return Arrays.asList(Serotype.values()).stream().noneMatch(s -> s.name().equals(serotypeEnumVal)) ? OTHER : Serotype.valueOf(serotypeEnumVal);
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
