/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.climate;

import de.symeda.sormas.api.i18n.I18nProperties;

/**
 * Why a week has no usual range for cases.
 */
public enum ClimateUsualRangeMissing {

	/** Fewer reference years with cases of the disease than CLIMATE_CASE_MIN_HISTORY_YEARS, after excluded years. */
	NOT_ENOUGH_HISTORY,
	/** The median weekly count at this time of year is below CLIMATE_CASE_MIN_WEEKLY_CASES. */
	TOO_FEW_CASES;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
