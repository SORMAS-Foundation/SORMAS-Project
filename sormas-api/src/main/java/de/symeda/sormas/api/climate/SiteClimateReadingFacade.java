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
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.environment.EnvironmentReferenceDto;

/**
 * Spot measurements taken at environments (site readings). They are shown on the environment's Climate data tab beside
 * the daily record, never mixed into it, and never feed the dashboard. Readings are recorded with the environment edit
 * right, so environmental surveillance users need no climate right to record them.
 */
@Remote
public interface SiteClimateReadingFacade {

	/**
	 * @return the environment's readings taken from {@code from} to {@code to} (both days included), newest first
	 */
	List<SiteClimateReadingDto> getReadings(EnvironmentReferenceDto environment, LocalDate from, LocalDate to);

	/**
	 * Saves a new or changed reading; the reporting user of a new reading is the current user.
	 *
	 * @throws de.symeda.sormas.api.utils.ValidationRuntimeException
	 *             when the reading is invalid or its environment may not be edited
	 */
	SiteClimateReadingDto saveReading(SiteClimateReadingDto reading);

	/**
	 * Deletes a reading; its earlier values stay in the change history.
	 *
	 * @throws de.symeda.sormas.api.utils.ValidationRuntimeException
	 *             when the reading is unknown or its environment may not be edited
	 */
	void deleteReading(String readingUuid);
}
