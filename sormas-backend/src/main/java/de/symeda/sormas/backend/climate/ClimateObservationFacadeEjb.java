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

import static de.symeda.sormas.api.utils.FieldConstraints.CHARACTER_LIMIT_DEFAULT;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateImportRowResult;
import de.symeda.sormas.api.climate.ClimateImportRowResult.Status;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationFacade;
import de.symeda.sormas.api.climate.ClimateObservationValidation;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ClimateObservationFacade")
public class ClimateObservationFacadeEjb implements ClimateObservationFacade {

	@EJB
	private ClimateSettings climateSettings;
	@EJB
	private ClimateObservationService climateObservationService;
	@EJB
	private ClimateLocationService climateLocationService;
	@EJB
	private ClimateLinkingService climateLinkingService;
	@EJB
	private DistrictService districtService;
	@EJB
	private EnvironmentService environmentService;
	@EJB
	private UserService userService;

	@Override
	@RightsAllowed(UserRight._CLIMATE_IMPORT)
	public List<ClimateImportRowResult> importEntries(List<ClimateObservationEntryDto> entries, boolean overwriteImports, Date runStartedAt) {

		checkClimateEnabled();
		if (entries.size() > MAX_IMPORT_ENTRIES) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateEntryTooMany, MAX_IMPORT_ENTRIES));
		}

		User user = userService.getCurrentUser();
		long runStart = runStartedAt != null ? runStartedAt.getTime() : System.currentTimeMillis();
		LocalDate today = LocalDate.now();
		PlaceResolver places = new PlaceResolver();

		ClimateImportRowResult[] results = new ClimateImportRowResult[entries.size()];
		Place[] placeOfEntry = new Place[entries.size()];
		LocalDate[] dayOfEntry = new LocalDate[entries.size()];
		Map<Long, List<Integer>> entriesByLocation = new LinkedHashMap<>();

		for (int i = 0; i < entries.size(); i++) {
			ClimateObservationEntryDto entry = entries.get(i);
			String error = ClimateObservationValidation.validate(entry, today);
			if (error == null) {
				try {
					placeOfEntry[i] = places.resolve(entry);
				} catch (ValidationRuntimeException e) {
					error = e.getMessage();
				}
			}
			if (error != null) {
				results[i] = new ClimateImportRowResult(Status.REJECTED, error);
				continue;
			}
			ClimateObservationValidation.fillMeanTemperature(entry);
			dayOfEntry[i] = ClimateObservationValidation.parseDate(entry.getDate());
			entriesByLocation.computeIfAbsent(placeOfEntry[i].location.getId(), id -> new ArrayList<>()).add(i);
		}

		// the rows already held, and the districts sharing each location: one query per location
		Map<Long, Map<LocalDate, ClimateObservation>> rowsByLocation = new HashMap<>();
		entriesByLocation.forEach((locationId, indexes) -> {
			LocalDate first = indexes.stream().map(i -> dayOfEntry[i]).min(LocalDate::compareTo).get();
			LocalDate last = indexes.stream().map(i -> dayOfEntry[i]).max(LocalDate::compareTo).get();
			rowsByLocation.put(locationId, climateObservationService.getByLocationAndPeriod(placeOfEntry[indexes.get(0)].location, first, last));
		});
		Map<Long, List<String>> districtNamesByLocation = climateLocationService.getDistrictNamesByLocation(entriesByLocation.keySet());

		Map<String, Integer> writtenNow = new HashMap<>();
		for (int i = 0; i < entries.size(); i++) {
			if (results[i] != null) {
				continue;
			}
			Place place = placeOfEntry[i];
			Long locationId = place.location.getId();
			LocalDate day = dayOfEntry[i];
			String key = locationId + ":" + day;
			Map<LocalDate, ClimateObservation> rows = rowsByLocation.get(locationId);
			ClimateObservation row = rows.get(day);

			boolean writtenThisRun = writtenNow.containsKey(key)
				|| (row != null
					&& row.getSource() == ClimateDataSource.IMPORT
					&& row.getChangeDate() != null
					&& row.getChangeDate().getTime() >= runStart);

			if (row != null && row.getSource() == ClimateDataSource.MANUAL) {
				results[i] = new ClimateImportRowResult(Status.SKIPPED, I18nProperties.getValidationError(Validations.climateEntryManualKept));
			} else if (writtenThisRun) {
				List<String> sharing = new ArrayList<>(districtNamesByLocation.getOrDefault(locationId, Collections.emptyList()));
				sharing.remove(place.districtName);
				if (place.districtName != null && !sharing.isEmpty()) {
					results[i] = new ClimateImportRowResult(
						Status.SKIPPED,
						I18nProperties.getValidationError(Validations.climateEntrySharedCell, place.districtName, String.join(", ", sharing)));
				} else {
					results[i] = new ClimateImportRowResult(Status.REJECTED, I18nProperties.getValidationError(Validations.climateEntryDuplicate));
				}
			} else if (row != null && row.getSource() == ClimateDataSource.IMPORT && !overwriteImports) {
				results[i] = new ClimateImportRowResult(Status.SKIPPED, I18nProperties.getValidationError(Validations.climateEntryImportKept));
			} else {
				if (row == null) {
					row = newRow(place.location, day);
					rows.put(day, row);
				}
				setEntryValues(row, entries.get(i), ClimateDataSource.IMPORT, user);
				climateObservationService.ensurePersisted(row);
				writtenNow.put(key, i);
				results[i] = ClimateImportRowResult.saved();
			}
		}

		climateObservationService.doFlush();
		return Arrays.asList(results);
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_EDIT)
	public void saveManualEntry(ClimateObservationEntryDto entry) {

		checkClimateEnabled();
		String error = ClimateObservationValidation.validate(entry, LocalDate.now());
		if (error != null) {
			throw new ValidationRuntimeException(error);
		}
		Place place = new PlaceResolver().resolve(entry);
		ClimateObservationValidation.fillMeanTemperature(entry);
		LocalDate day = ClimateObservationValidation.parseDate(entry.getDate());

		ClimateObservation row = climateObservationService.getByLocationAndPeriod(place.location, day, day).get(day);
		if (row == null) {
			row = newRow(place.location, day);
		}
		setEntryValues(row, entry, ClimateDataSource.MANUAL, userService.getCurrentUser());
		climateObservationService.ensurePersisted(row);
	}

	private void checkClimateEnabled() {
		if (!climateSettings.isClimateEnabled()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateFeatureOff));
		}
	}

	private static ClimateObservation newRow(ClimateLocation location, LocalDate day) {
		ClimateObservation row = new ClimateObservation();
		row.setClimateLocation(location);
		row.setObservationDate(ClimateCalendar.toDate(day));
		return row;
	}

	/**
	 * Replaces all of the row's values: a day's values come together from one source, so a value the entry leaves empty is
	 * empty afterwards.
	 */
	private static void setEntryValues(ClimateObservation row, ClimateObservationEntryDto entry, ClimateDataSource source, User user) {
		row.setTemperatureMin(entry.getTemperatureMin());
		row.setTemperatureMax(entry.getTemperatureMax());
		row.setTemperatureMean(entry.getTemperatureMean());
		row.setRainfall(entry.getRainfall());
		row.setRelativeHumidity(entry.getRelativeHumidity());
		row.setHumidityMeasure(entry.getRelativeHumidity() != null ? entry.getHumidityMeasure() : null);
		row.setSource(source);
		String sourceDetail = StringUtils.isNotBlank(entry.getSourceDetail()) ? entry.getSourceDetail().trim() : user != null ? user.getUserName() : null;
		row.setSourceDetail(StringUtils.truncate(sourceDetail, CHARACTER_LIMIT_DEFAULT));
		row.setReportingUser(user);
	}

	/**
	 * The climate location an entry is written to, and the district it was given for (to name it when two districts share
	 * one location).
	 */
	private static class Place {

		private final ClimateLocation location;
		private final String districtName;

		Place(ClimateLocation location, String districtName) {
			this.location = location;
			this.districtName = districtName;
		}
	}

	/**
	 * Finds the climate location for an entry's key. Remembers what it found, because an import names the same few
	 * districts in every row.
	 */
	private class PlaceResolver {

		private final Map<String, Place> places = new HashMap<>();

		Place resolve(ClimateObservationEntryDto entry) {

			if (StringUtils.isNotBlank(entry.getDistrictUuid())) {
				String uuid = entry.getDistrictUuid().trim();
				return found(places.computeIfAbsent("district:" + uuid, k -> forDistrict(districtService.getByUuid(uuid))), uuid);
			}
			if (StringUtils.isNotBlank(entry.getDistrictExternalId())) {
				String externalId = entry.getDistrictExternalId().trim();
				return found(places.computeIfAbsent("external:" + externalId, k -> {
					List<District> districts = districtService.getByExternalId(externalId, false);
					return forDistrict(districts.size() == 1 ? districts.get(0) : null);
				}), externalId);
			}
			if (StringUtils.isNotBlank(entry.getEnvironmentUuid())) {
				Environment environment = environmentService.getByUuid(entry.getEnvironmentUuid());
				if (environment == null || environment.isDeleted()) {
					throw new ValidationRuntimeException(
						I18nProperties.getValidationError(Validations.climateEntryEnvironmentNotFound, entry.getEnvironmentUuid()));
				}
				if (environment.getClimateLocation() == null) {
					throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateEntryEnvironmentNoGps));
				}
				return new Place(environment.getClimateLocation(), null);
			}
			ClimateLocation location = climateLocationService.getByUuid(entry.getClimateLocationUuid());
			if (location == null) {
				throw new ValidationRuntimeException(
					I18nProperties.getValidationError(Validations.climateEntryLocationNotFound, entry.getClimateLocationUuid()));
			}
			return new Place(location, null);
		}

		/**
		 * @return the district's place, or {@link #NO_DISTRICT} (cached too, so an unknown district is looked up once)
		 */
		private Place forDistrict(District district) {
			if (district == null) {
				return NO_DISTRICT;
			}
			if (district.getClimateLocation() == null) {
				// a district created while climate data was off, not yet linked by the nightly run
				climateLinkingService.linkDistrict(district);
			}
			return new Place(district.getClimateLocation(), district.getName());
		}

		private Place found(Place place, String key) {
			if (place == NO_DISTRICT) {
				throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateEntryDistrictNotFound, key));
			}
			return place;
		}
	}

	private static final Place NO_DISTRICT = new Place(null, null);

	@LocalBean
	@Stateless
	public static class ClimateObservationFacadeEjbLocal extends ClimateObservationFacadeEjb {
	}
}
