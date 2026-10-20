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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import de.symeda.sormas.api.Disease;

/**
 * Keys of the climate settings in system configuration (category {@link #CATEGORY_NAME}) and other fixed climate values.
 */
public final class ClimateConfiguration {

	public static final String CATEGORY_NAME = "CLIMATE";

	public static final String DAILY_SOURCE = "CLIMATE_DAILY_SOURCE";
	public static final String HISTORY_SOURCE = "CLIMATE_HISTORY_SOURCE";
	public static final String OPENWEATHER_API_KEY = "CLIMATE_OPENWEATHER_API_KEY";
	public static final String GRID_SIZE = "CLIMATE_GRID_SIZE";
	public static final String FETCH_CATCHUP_DAYS = "CLIMATE_FETCH_CATCHUP_DAYS";
	public static final String HISTORY_YEARS = "CLIMATE_HISTORY_YEARS";
	public static final String MIN_HISTORY_YEARS = "CLIMATE_MIN_HISTORY_YEARS";
	public static final String RAINFALL_MIN_MM = "CLIMATE_RAINFALL_MIN_MM";
	public static final String CASE_MIN_HISTORY_YEARS = "CLIMATE_CASE_MIN_HISTORY_YEARS";
	public static final String CASE_MIN_WEEKLY_CASES = "CLIMATE_CASE_MIN_WEEKLY_CASES";

	/** Default for CLIMATE_HISTORY_YEARS: reference years for normal ranges, and the most years offered for exclusion. */
	public static final int DEFAULT_HISTORY_YEARS = 5;

	/** How many complete years the disease configuration offers as excluded years for the case usual range. */
	public static final int EXCLUDABLE_YEARS = 10;

	/**
	 * Diseases that are climate-sensitive when their disease configuration leaves the flag empty. Kept in code rather than
	 * set by SQL, because disease configuration rows of a fresh install are created at server start, after the SQL has run.
	 */
	public static final Set<Disease> DEFAULT_CLIMATE_SENSITIVE_DISEASES = Collections.unmodifiableSet(
		EnumSet.of(
			Disease.MALARIA,
			Disease.DENGUE,
			Disease.CHIKUNGUNYA,
			Disease.YELLOW_FEVER,
			Disease.WEST_NILE_FEVER,
			Disease.CHOLERA,
			Disease.TYPHOID_FEVER,
			Disease.DIARRHEA_DEHYDRATION,
			Disease.DIARRHEA_BLOOD,
			Disease.CSM,
			Disease.LASSA));

	private ClimateConfiguration() {
	}
}
