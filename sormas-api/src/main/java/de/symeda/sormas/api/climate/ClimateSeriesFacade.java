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

import java.time.LocalDate;

import javax.ejb.Remote;

import de.symeda.sormas.api.environment.EnvironmentReferenceDto;

/**
 * Climate figures for the Climate dashboard and the environment Climate data tab, worked out from the best-available
 * daily rows when asked for: weekly roll-ups, region values, normal ranges, unusual weeks and last year's values.
 */
@Remote
public interface ClimateSeriesFacade {

	/**
	 * @return the weekly series of the criteria's district, or of its region when no district is given, with the series of
	 *         each of the region's districts added when the criteria asks for them
	 */
	ClimateWeeklySeriesDto getWeeklySeries(ClimateSeriesCriteria criteria);

	/**
	 * @return the environment's daily climate from {@code from} to {@code to} (both included), with daily normals and the
	 *         summary cards for the 7 days ending on the latest day with data
	 */
	ClimateDailySeriesDto getDailySeries(EnvironmentReferenceDto environment, LocalDate from, LocalDate to);
}
