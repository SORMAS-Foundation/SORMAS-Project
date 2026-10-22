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

package de.symeda.sormas.backend.climate;

import java.time.LocalDate;

import de.symeda.sormas.api.climate.ClimateDataSource;

/**
 * A source of daily climate data that SORMAS calls itself. Phase 1 has one, {@link OpenWeatherProvider}; another provider
 * is added as one more implementation. One instance serves one job run and paces its own calls; jobs do the windowing,
 * counting and stopping, so each {@link #fetchWindow} is one billable call (plus retries).
 */
public interface ClimateDataProvider extends AutoCloseable {

	ClimateDataSource source();

	/**
	 * @return how many consecutive days one call returns
	 */
	int maxDaysPerCall();

	/**
	 * Fetches up to {@link #maxDaysPerCall()} days starting on {@code firstDay}. Only days that have ended at the location
	 * are returned, never forecast days. A day the provider has no record for is left out; it is never returned as zero.
	 */
	ClimateFetchResult fetchWindow(double latitude, double longitude, LocalDate firstDay) throws ClimateProviderException;

	@Override
	void close();
}
