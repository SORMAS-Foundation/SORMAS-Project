/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.api;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum CaseMeasure {

	/**
	 * Number of cases
	 */
	CASE_COUNT,
	/**
	 * Number of cases per DistrictDto.CASE_INCIDENCE_DIVISOR; rounded to two decimal places with rounding mode half up
	 */
	CASE_INCIDENCE;

	public String toString() {
		return I18nProperties.getEnumCaption(this);
	};
}
