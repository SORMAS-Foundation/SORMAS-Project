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

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

/**
 * Daily climate values from met agencies and data managers. The source order is applied when a row is written: a manual
 * value always wins; an import replaces OpenWeather values, and earlier imports only when asked to; a manual value is never
 * replaced by an import. Earlier values stay in the change history.
 */
@Remote
public interface ClimateObservationFacade {

	/** The most rows {@link #importEntries} takes in one call. */
	int MAX_IMPORT_ENTRIES = 1000;

	/**
	 * Saves imported or pushed rows in one transaction, with source IMPORT.
	 *
	 * @param overwriteImports
	 *            whether a row replaces an earlier import of the same place and day (push always does)
	 * @param runStartedAt
	 *            when the import began. A row for a place and day that this import has already written is a duplicate;
	 *            for two districts sharing one climate location the first row is kept.
	 * @return one result per entry, in the same order
	 */
	List<ClimateImportRowResult> importEntries(List<ClimateObservationEntryDto> entries, boolean overwriteImports, Date runStartedAt);

	/**
	 * Saves a manual value for a place and day. It wins over every other source.
	 *
	 * @throws de.symeda.sormas.api.utils.ValidationRuntimeException
	 *             when the entry is invalid or its place is unknown
	 */
	void saveManualEntry(ClimateObservationEntryDto entry);
}
