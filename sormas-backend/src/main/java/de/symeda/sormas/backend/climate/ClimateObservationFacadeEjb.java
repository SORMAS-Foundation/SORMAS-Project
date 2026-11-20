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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDistrictDto;
import de.symeda.sormas.api.climate.ClimateEarlierValueDto;
import de.symeda.sormas.api.climate.ClimateHistoryCoverageDto;
import de.symeda.sormas.api.climate.ClimateImportRowResult;
import de.symeda.sormas.api.climate.ClimateImportRowResult.Status;
import de.symeda.sormas.api.climate.ClimateObservationCriteria;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationExportDto;
import de.symeda.sormas.api.climate.ClimateObservationFacade;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.climate.ClimateObservationValidation;
import de.symeda.sormas.api.environment.EnvironmentReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.climate.ClimateLocationService.PlaceNames;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ClimateObservationFacade")
public class ClimateObservationFacadeEjb implements ClimateObservationFacade {

	/** The most earlier values {@link #getEarlierValues} returns. */
	private static final int MAX_EARLIER_VALUES = 20;

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
	private RegionService regionService;
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
					// strictly after: a row an earlier import wrote in the same millisecond is not this import's
					&& row.getChangeDate().getTime() > runStart);

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

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public List<ClimateObservationIndexDto> getIndexList(
		ClimateObservationCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		List<ClimateObservation> rows = climateObservationService.getPage(criteria, first, max, sortProperties, false);
		Map<Long, PlaceNames> names = getPlaceNames(rows);
		List<ClimateObservationIndexDto> result = new ArrayList<>(rows.size());
		rows.forEach(row -> result.add(toIndexDto(row, row.getClimateLocation(), names.get(row.getClimateLocation().getId()))));
		return result;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public long count(ClimateObservationCriteria criteria) {
		return climateObservationService.count(criteria);
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_EXPORT)
	public List<ClimateObservationExportDto> getExportList(ClimateObservationCriteria criteria, Integer first, Integer max) {

		List<ClimateObservation> rows = climateObservationService.getPage(criteria, first, max, null, true);
		Map<Long, PlaceNames> names = getPlaceNames(rows);
		List<ClimateObservationExportDto> result = new ArrayList<>(rows.size());
		for (ClimateObservation row : rows) {
			ClimateLocation location = row.getClimateLocation();
			PlaceNames places = names.getOrDefault(location.getId(), PlaceNames.NONE);
			ClimateObservationExportDto export = new ClimateObservationExportDto();
			export.setUuid(row.getUuid());
			export.setObservationDate(row.getObservationDate());
			export.setRegions(String.join(", ", places.getRegions()));
			export.setDistricts(String.join(", ", places.getDistricts()));
			export.setEnvironments(String.join(", ", places.getEnvironments()));
			export.setLatitude(location.getLatitude());
			export.setLongitude(location.getLongitude());
			export.setTemperatureMin(row.getTemperatureMin());
			export.setTemperatureMean(row.getTemperatureMean());
			export.setTemperatureMax(row.getTemperatureMax());
			export.setRainfall(row.getRainfall());
			export.setRelativeHumidity(row.getRelativeHumidity());
			export.setHumidityMeasure(row.getHumidityMeasure());
			export.setSource(row.getSource());
			export.setSourceDetail(row.getSourceDetail());
			export.setReportingUser(row.getReportingUser() != null ? row.getReportingUser().getUserName() : null);
			export.setChangeDate(row.getChangeDate());
			result.add(export);
		}
		return result;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public ClimateObservationIndexDto getObservation(ClimateObservationEntryDto key) {

		LocalDate day = ClimateObservationValidation.parseDate(key.getDate());
		if (day == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateEntryDate));
		}
		ClimateLocation location = new PlaceResolver().resolve(key).location;
		ClimateObservation row = climateObservationService.getByLocationAndPeriod(location, day, day).get(day);
		ClimateObservationIndexDto observation =
			toIndexDto(row, location, climateLocationService.getPlaceNames(Collections.singleton(location.getId())).get(location.getId()));
		observation.setObservationDate(ClimateCalendar.toDate(day));
		return observation;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public List<ClimateEarlierValueDto> getEarlierValues(String observationUuid) {
		return climateObservationService.getEarlierValues(observationUuid, MAX_EARLIER_VALUES);
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_EDIT)
	public void deleteObservation(String observationUuid) {

		checkClimateEnabled();
		ClimateObservation row = climateObservationService.getByUuid(observationUuid);
		if (row == null || row.getSource() == ClimateDataSource.OPENWEATHER) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateObservationNotDeletable));
		}
		climateObservationService.deletePermanent(row);
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public List<ClimateDistrictDto> getDistrictsWithoutPoint() {

		List<ClimateDistrictDto> districts = new ArrayList<>();
		for (Object[] row : climateLocationService.getDistrictsWithLocation(null)) {
			if (row[5] == null) {
				districts.add(new ClimateDistrictDto(regionReference(row), districtReference(row)));
			}
		}
		return districts;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public List<ClimateHistoryCoverageDto> getHistoryCoverage(RegionReferenceDto region) {

		Long regionId = region != null ? regionService.getByReferenceDto(region).getId() : null;
		Map<Long, List<Object[]>> daysByLocation = new HashMap<>();
		for (Object[] row : climateObservationService.getDaysByYearAndSource(regionId)) {
			daysByLocation.computeIfAbsent((Long) row[0], id -> new ArrayList<>()).add(row);
		}
		List<ClimateHistoryCoverageDto> coverage = new ArrayList<>();
		for (Object[] row : climateLocationService.getDistrictsWithLocation(regionId)) {
			ClimateHistoryCoverageDto district = new ClimateHistoryCoverageDto(regionReference(row), districtReference(row), row[5] != null);
			for (Object[] days : daysByLocation.getOrDefault((Long) row[4], Collections.emptyList())) {
				district.addDays(
					((Number) days[1]).intValue(),
					(ClimateDataSource) days[2],
					((Number) days[3]).intValue(),
					ClimateCalendar.toLocalDate((Date) days[4]));
			}
			coverage.add(district);
		}
		return coverage;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public List<EnvironmentReferenceDto> getEnvironmentsWithClimateLocation(RegionReferenceDto region, DistrictReferenceDto district) {

		List<EnvironmentReferenceDto> environments = new ArrayList<>();
		climateLocationService
			.getEnvironmentsWithLocation(region != null ? region.getUuid() : null, district != null ? district.getUuid() : null)
			.forEach(row -> environments.add(new EnvironmentReferenceDto((String) row[0], (String) row[1])));
		return environments;
	}

	private static RegionReferenceDto regionReference(Object[] row) {
		return new RegionReferenceDto((String) row[0], (String) row[1], null);
	}

	private static DistrictReferenceDto districtReference(Object[] row) {
		return new DistrictReferenceDto((String) row[2], (String) row[3], null);
	}

	private Map<Long, PlaceNames> getPlaceNames(List<ClimateObservation> rows) {
		Set<Long> locationIds = new HashSet<>();
		rows.forEach(row -> locationIds.add(row.getClimateLocation().getId()));
		return climateLocationService.getPlaceNames(locationIds);
	}

	/**
	 * @param row
	 *            the observation, or null for a location and day without a row
	 */
	static ClimateObservationIndexDto toIndexDto(ClimateObservation row, ClimateLocation location, PlaceNames places) {

		ClimateObservationIndexDto dto = new ClimateObservationIndexDto();
		dto.setClimateLocationUuid(location.getUuid());
		dto.setLatitude(location.getLatitude());
		dto.setLongitude(location.getLongitude());
		if (places != null) {
			dto.setDistrictNames(new ArrayList<>(places.getDistricts()));
			dto.setEnvironmentNames(new ArrayList<>(places.getEnvironments()));
		}
		if (row != null) {
			dto.setUuid(row.getUuid());
			dto.setObservationDate(row.getObservationDate());
			dto.setTemperatureMin(row.getTemperatureMin());
			dto.setTemperatureMean(row.getTemperatureMean());
			dto.setTemperatureMax(row.getTemperatureMax());
			dto.setRainfall(row.getRainfall());
			dto.setRelativeHumidity(row.getRelativeHumidity());
			dto.setHumidityMeasure(row.getHumidityMeasure());
			dto.setSource(row.getSource());
			dto.setSourceDetail(row.getSourceDetail());
		}
		return dto;
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
