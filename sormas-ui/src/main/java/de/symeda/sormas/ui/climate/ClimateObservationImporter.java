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

package de.symeda.sormas.ui.climate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.climate.ClimateImportRowResult;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationValidation;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportErrorException;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.ui.importer.DataImporter;
import de.symeda.sormas.ui.importer.ImportLineResult;

/**
 * Imports daily climate values from a met agency CSV file: one row per district and day. Each row is checked when it is
 * read, and good rows are saved in batches of {@link #BATCH_SIZE} (one transaction each), because a history file can hold
 * over a million rows. Rows the server does not save (an earlier import kept, a manual value kept, a day already imported
 * in this file) are written to the error report with the reason.
 */
public class ClimateObservationImporter extends DataImporter {

	static final int BATCH_SIZE = 500;

	private final boolean overwriteImports;
	private final Date runStartedAt = new Date();
	private final Map<String, String> districtUuidByName = new HashMap<>();
	private final List<ClimateObservationEntryDto> batch = new ArrayList<>();
	private final List<String[]> batchLines = new ArrayList<>();

	public ClimateObservationImporter(File inputFile, UserDto currentUser, ValueSeparator csvSeparator, boolean overwriteImports)
		throws IOException {
		super(inputFile, false, currentUser, csvSeparator);
		this.overwriteImports = overwriteImports;
	}

	@Override
	protected ImportLineResult importDataFromCsvLine(
		String[] values,
		String[] entityClasses,
		String[] entityProperties,
		String[][] entityPropertyPaths,
		boolean firstLine)
		throws IOException, InvalidColumnException, InterruptedException {

		if (values.length > entityProperties.length) {
			writeImportError(values, I18nProperties.getValidationError(Validations.importLineTooLong));
			return ImportLineResult.ERROR;
		}

		ClimateObservationEntryDto entry = new ClimateObservationEntryDto();
		String region = null;
		String district = null;
		for (int i = 0; i < values.length; i++) {
			String property = entityProperties[i].trim();
			String value = StringUtils.trimToNull(values[i]);
			try {
				if (ClimateObservationEntryDto.REGION.equalsIgnoreCase(property)) {
					region = value;
				} else if (ClimateObservationEntryDto.DISTRICT.equalsIgnoreCase(property)) {
					district = value;
				} else if (ClimateObservationEntryDto.DISTRICT_EXTERNAL_ID.equalsIgnoreCase(property)) {
					entry.setDistrictExternalId(value);
				} else if (ClimateObservationEntryDto.DATE.equalsIgnoreCase(property)) {
					entry.setDate(value);
				} else if (ClimateObservationEntryDto.TEMPERATURE_MIN.equalsIgnoreCase(property)) {
					entry.setTemperatureMin(parseNumber(value));
				} else if (ClimateObservationEntryDto.TEMPERATURE_MAX.equalsIgnoreCase(property)) {
					entry.setTemperatureMax(parseNumber(value));
				} else if (ClimateObservationEntryDto.TEMPERATURE_MEAN.equalsIgnoreCase(property)) {
					entry.setTemperatureMean(parseNumber(value));
				} else if (ClimateObservationEntryDto.RAINFALL.equalsIgnoreCase(property)) {
					entry.setRainfall(parseNumber(value));
				} else if (ClimateObservationEntryDto.RELATIVE_HUMIDITY.equalsIgnoreCase(property)) {
					entry.setRelativeHumidity(parseNumber(value));
				} else if (ClimateObservationEntryDto.HUMIDITY_MEASURE.equalsIgnoreCase(property)) {
					entry.setHumidityMeasure(value != null ? HumidityMeasure.valueOf(value.toUpperCase(Locale.ROOT)) : null);
				} else if (ClimateObservationEntryDto.SOURCE_DETAIL.equalsIgnoreCase(property)) {
					entry.setSourceDetail(value);
				} else {
					throw new InvalidColumnException(property);
				}
			} catch (IllegalArgumentException e) {
				// also NumberFormatException
				writeImportError(values, new ImportErrorException(values[i], property).getMessage());
				return ImportLineResult.ERROR;
			}
		}

		if (district != null) {
			String districtUuid = entry.getDistrictExternalId() == null ? findDistrict(region, district) : null;
			if (districtUuid == null) {
				String message = entry.getDistrictExternalId() != null
					? I18nProperties.getValidationError(Validations.climateEntryLocationKey)
					: I18nProperties.getValidationError(Validations.climateEntryDistrictNotFound, region != null ? region + " / " + district : district);
				writeImportError(values, message);
				return ImportLineResult.ERROR;
			}
			entry.setDistrictUuid(districtUuid);
		}

		String error = ClimateObservationValidation.validate(entry, LocalDate.now());
		if (error != null) {
			writeImportError(values, error);
			return ImportLineResult.ERROR;
		}

		batch.add(entry);
		batchLines.add(values);
		if (batch.size() >= BATCH_SIZE) {
			saveBatch();
		}
		return ImportLineResult.SUCCESS;
	}

	@Override
	protected void finishImport() throws IOException {
		saveBatch();
	}

	private void saveBatch() throws IOException {

		if (batch.isEmpty()) {
			return;
		}
		try {
			List<ClimateImportRowResult> results = FacadeProvider.getClimateObservationFacade().importEntries(batch, overwriteImports, runStartedAt);
			for (int i = 0; i < results.size(); i++) {
				if (results.get(i).getStatus() != ClimateImportRowResult.Status.SAVED) {
					writeImportError(batchLines.get(i), results.get(i).getMessage());
				}
			}
		} catch (ValidationRuntimeException e) {
			for (String[] line : batchLines) {
				writeImportError(line, e.getMessage());
			}
		} finally {
			batch.clear();
			batchLines.clear();
		}
	}

	/**
	 * @return the uuid of the one district with this name (in this region, when given), or null. Remembered for the whole
	 *         file, so a file of a million rows looks up each district once.
	 */
	private String findDistrict(String region, String district) {

		String key = StringUtils.lowerCase(region) + "|" + district.toLowerCase();
		String uuid = districtUuidByName.get(key);
		if (uuid == null) {
			uuid = "";
			RegionReferenceDto regionReference = null;
			List<RegionReferenceDto> regions = region != null ? FacadeProvider.getRegionFacade().getReferencesByName(region, false) : null;
			if (regions == null || regions.size() == 1) {
				regionReference = regions != null ? regions.get(0) : null;
				List<DistrictReferenceDto> districts = FacadeProvider.getDistrictFacade().getByName(district, regionReference, false);
				if (districts.size() == 1) {
					uuid = districts.get(0).getUuid();
				}
			}
			districtUuidByName.put(key, uuid);
		}
		return uuid.isEmpty() ? null : uuid;
	}

	private static Double parseNumber(String value) {
		return value != null ? Double.valueOf(value.replace(',', '.')) : null;
	}
}
