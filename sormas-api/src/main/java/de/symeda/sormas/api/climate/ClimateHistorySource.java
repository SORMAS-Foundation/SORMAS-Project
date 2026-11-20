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
 * Where a deployment's climate history before switch-on comes from (system configuration CLIMATE_HISTORY_SOURCE).
 */
public enum ClimateHistorySource {

	/** History builds forward from the first nightly fetch. */
	NONE,
	/** Backfill from OpenWeather is available, and new locations are filled in automatically (charged per call). */
	OPENWEATHER,
	/** History comes by import or push; backfill is not offered. */
	EXTERNAL;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
