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

import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

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

	/**
	 * @return a page of observations matching the criteria, by default newest day first
	 */
	List<ClimateObservationIndexDto> getIndexList(ClimateObservationCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	long count(ClimateObservationCriteria criteria);

	/**
	 * @return a page of the export rows of the observations matching the criteria, newest day first
	 */
	List<ClimateObservationExportDto> getExportList(ClimateObservationCriteria criteria, Integer first, Integer max);

	/**
	 * @param key
	 *            a place (district, environment or climate location) and a date, as for {@link #saveManualEntry}
	 * @return the place's location and day with its current values, if any, and the places that share the location
	 * @throws de.symeda.sormas.api.utils.ValidationRuntimeException
	 *             when the place is unknown or the date is not a date
	 */
	ClimateObservationIndexDto getObservation(ClimateObservationEntryDto key);

	/**
	 * @return the values the observation held before later saves replaced them, newest first (at most 20)
	 */
	List<ClimateEarlierValueDto> getEarlierValues(String observationUuid);

	/**
	 * Deletes a manual or imported value, with its change history. OpenWeather rows cannot be deleted: the
	 * nightly fetch would bring recent days back, and older days are filled again by fetch now.
	 *
	 * @throws de.symeda.sormas.api.utils.ValidationRuntimeException
	 *             when the row is unknown or came from OpenWeather
	 */
	void deleteObservation(String observationUuid);

	/**
	 * @return the districts that are not archived and whose climate location has no coordinates, so no API data is fetched
	 *         for them, sorted by region and district name
	 */
	List<ClimateDistrictDto> getDistrictsWithoutPoint();

	/**
	 * @return per district that is not archived (of the region, or all), the history its climate location holds, sorted by
	 *         region and district name
	 */
	List<ClimateHistoryCoverageDto> getHistoryCoverage(RegionReferenceDto region);

	/**
	 * @return the environments (not deleted) of the district, or else the region, or else all, that have a climate record of
	 *         their own because their GPS is usable, sorted by name
	 */
	List<EnvironmentReferenceDto> getEnvironmentsWithClimateLocation(RegionReferenceDto region, DistrictReferenceDto district);
}
