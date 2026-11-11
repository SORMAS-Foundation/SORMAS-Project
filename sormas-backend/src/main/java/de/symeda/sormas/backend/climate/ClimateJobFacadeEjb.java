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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.climate.ClimateCallEstimateDto;
import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.climate.ClimateCosts;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateJobCriteria;
import de.symeda.sormas.api.climate.ClimateJobFacade;
import de.symeda.sormas.api.climate.ClimateJobIndexDto;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.climate.ClimateRelinkDayDto;
import de.symeda.sormas.api.climate.ClimateSummaryDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.climate.ClimateLocationService.PlaceNames;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "ClimateJobFacade")
@RightsAllowed(UserRight._CLIMATE_MANAGE_SOURCES)
public class ClimateJobFacadeEjb implements ClimateJobFacade {

	@EJB
	private ClimateSettings climateSettings;
	@EJB
	private ClimateJobService climateJobService;
	@EJB
	private ClimateLocationService climateLocationService;
	@EJB
	private ClimateObservationService climateObservationService;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private UserService userService;

	@Override
	public ClimateCallEstimateDto calculateBackfillCalls(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		boolean includeEnvironments,
		int years) {

		LocalDate from = backfillStart(years);
		LocalDate to = LocalDate.now().minusDays(1);
		long periodDays = to.toEpochDay() - from.toEpochDay() + 1;
		List<Long> locations = getScopeLocations(region, district, includeEnvironments);
		long days = 0;
		long calls = 0;
		for (Long locationId : locations) {
			Set<LocalDate> held = climateObservationService.getHeldDays(locationId, from, to);
			days += periodDays - held.size();
			calls += ClimateCalendar.windowStarts(from, to, held, ClimateCosts.OPENWEATHER_DAYS_PER_CALL).size();
		}
		return new ClimateCallEstimateDto(locations.size(), days, calls);
	}

	@Override
	public String startBackfill(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments, int years, long confirmedCalls) {

		checkAvailable(climateSettings.isHistoryFromOpenWeather(), Validations.climateBackfillOff);
		ClimateJob job = buildJob(ClimateJobType.BACKFILL, region, district, includeEnvironments);
		job.setFromDate(ClimateCalendar.toDate(backfillStart(years)));
		job.setToDate(ClimateCalendar.toDate(LocalDate.now().minusDays(1)));
		job.setEstimatedCalls((int) Math.min(confirmedCalls, Integer.MAX_VALUE));
		climateJobService.ensurePersisted(job);
		return job.getUuid();
	}

	@Override
	public ClimateCallEstimateDto calculateFetchCalls(
		RegionReferenceDto region,
		DistrictReferenceDto district,
		boolean includeEnvironments,
		Date from,
		Date to) {

		LocalDate first = ClimateCalendar.toLocalDate(from);
		LocalDate last = ClimateCalendar.toLocalDate(to);
		checkFetchPeriod(first, last);
		long days = last.toEpochDay() - first.toEpochDay() + 1;
		int locations = getScopeLocations(region, district, includeEnvironments).size();
		return new ClimateCallEstimateDto(locations, locations * days, locations * ClimateCosts.callsPerLocation(days));
	}

	@Override
	public String fetchNow(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments, Date from, Date to) {

		checkAvailable(climateSettings.isDailyFromOpenWeather(), Validations.climateFetchOff);
		LocalDate first = ClimateCalendar.toLocalDate(from);
		LocalDate last = ClimateCalendar.toLocalDate(to);
		checkFetchPeriod(first, last);
		ClimateJob job = buildJob(ClimateJobType.FETCH, region, district, includeEnvironments);
		job.setFromDate(ClimateCalendar.toDate(first));
		job.setToDate(ClimateCalendar.toDate(last));
		climateJobService.ensurePersisted(job);
		return job.getUuid();
	}

	@Override
	public void cancel(String jobUuid) {

		ClimateJob job = climateJobService.getByUuid(jobUuid);
		if (job == null || !job.getStatus().isOpen()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateJobNotChangeable));
		}
		job.setStatus(ClimateJobStatus.CANCELLED);
		job.setLastError("Cancelled by " + userService.getCurrentUser().getUserName());
		job.setFinishedAt(new Date());
		climateJobService.ensurePersisted(job);
	}

	@Override
	public void startAgain(String jobUuid) {

		ClimateJob job = climateJobService.getByUuid(jobUuid);
		boolean resumable = job != null
			&& (job.getJobType() == ClimateJobType.BACKFILL || job.getJobType() == ClimateJobType.TOP_UP || job.getJobType() == ClimateJobType.FETCH)
			&& (job.getStatus() == ClimateJobStatus.PAUSED_AT_LIMIT
				|| job.getStatus() == ClimateJobStatus.FAILED
				|| job.getStatus() == ClimateJobStatus.CANCELLED);
		if (!resumable) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateJobNotChangeable));
		}
		job.setStatus(ClimateJobStatus.QUEUED);
		job.setPausedAt(null);
		job.setFinishedAt(null);
		climateJobService.ensurePersisted(job);
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public List<ClimateJobIndexDto> getIndexList(ClimateJobCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		List<ClimateJob> jobs = climateJobService.getPage(criteria, first, max);
		Set<Long> locationIds = new HashSet<>();
		jobs.stream().filter(job -> job.getClimateLocation() != null).forEach(job -> locationIds.add(job.getClimateLocation().getId()));
		Map<Long, PlaceNames> names = climateLocationService.getPlaceNames(locationIds);
		List<ClimateJobIndexDto> result = new ArrayList<>(jobs.size());
		jobs.forEach(job -> result.add(toIndexDto(job, names)));
		return result;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public long count(ClimateJobCriteria criteria) {
		return climateJobService.count(criteria);
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public ClimateSummaryDto getSummary() {

		ClimateSummaryDto summary = new ClimateSummaryDto();
		summary.setDailySource(climateSettings.getDailySource());
		summary.setHistorySource(climateSettings.getHistorySource());
		summary.setApiKeySet(climateSettings.getApiKey() != null);
		summary.setNightlyFetchActive(climateSettings.isNightlyFetchActive());
		summary.setHistoryYears(climateSettings.getHistoryYears());
		summary.setMinHistoryYears(climateSettings.getMinHistoryYears());

		Set<Long> districtLocations = new HashSet<>(climateLocationService.getDistrictLocationIds(null, null));
		summary.setDistrictLocations(districtLocations.size());
		summary.setEnvironmentOnlyLocations(
			(int) climateLocationService.getEnvironmentLocationIds(null, null).stream().filter(id -> !districtLocations.contains(id)).count());
		summary.setDistrictsWithoutPoint((int) climateLocationService.getDistrictsWithLocation(null).stream().filter(row -> row[5] == null).count());

		ClimateJob lastNightlyFetch = climateJobService.getLatestDailyFetch();
		if (lastNightlyFetch != null) {
			summary.setLastNightlyFetch(toIndexDto(lastNightlyFetch, Collections.emptyMap()));
		}
		return summary;
	}

	@Override
	@RightsAllowed(UserRight._CLIMATE_VIEW)
	public List<ClimateRelinkDayDto> getRelinkDays(String jobUuid) {

		ClimateJob job = climateJobService.getByUuid(jobUuid);
		if (job == null
			|| job.getJobType() != ClimateJobType.RELINK
			|| job.getFromClimateLocation() == null
			|| job.getClimateLocation() == null
			|| job.getFromDate() == null
			|| job.getToDate() == null) {
			return Collections.emptyList();
		}

		ClimateLocation from = job.getFromClimateLocation();
		ClimateLocation to = job.getClimateLocation();
		LocalDate first = ClimateCalendar.toLocalDate(job.getFromDate());
		LocalDate last = ClimateCalendar.toLocalDate(job.getToDate());
		Map<LocalDate, ClimateObservation> districtRows = climateObservationService.getByLocationAndPeriod(from, first, last);
		Map<LocalDate, ClimateObservation> keptRows = climateObservationService.getByLocationAndPeriod(to, first, last);
		Map<Long, PlaceNames> names = climateLocationService.getPlaceNames(Arrays.asList(from.getId(), to.getId()));

		List<ClimateRelinkDayDto> days = new ArrayList<>();
		districtRows.keySet().stream().sorted().forEach(day -> {
			ClimateObservation districtRow = districtRows.get(day);
			ClimateObservation keptRow = keptRows.get(day);
			// an external row that the new location's row of equal or higher rank kept out
			if (keptRow == null || districtRow.getSource() == ClimateDataSource.OPENWEATHER || districtRow.getSource().outranks(keptRow.getSource())) {
				return;
			}
			ClimateRelinkDayDto relinkDay = new ClimateRelinkDayDto(
				districtRow.getObservationDate(),
				ClimateObservationFacadeEjb.toIndexDto(districtRow, from, names.get(from.getId())),
				ClimateObservationFacadeEjb.toIndexDto(keptRow, to, names.get(to.getId())));
			// a day the relink copied holds the same values, saved no later than the move; a day corrected since was saved later
			if (!relinkDay.isSameValues() || keptRow.getChangeDate().after(job.getStartedAt())) {
				days.add(relinkDay);
			}
		});
		return days;
	}

	private static ClimateJobIndexDto toIndexDto(ClimateJob job, Map<Long, PlaceNames> names) {

		ClimateJobIndexDto dto = new ClimateJobIndexDto();
		dto.setUuid(job.getUuid());
		dto.setJobType(job.getJobType());
		dto.setStatus(job.getStatus());
		dto.setRegionName(job.getRegion() != null ? job.getRegion().getName() : null);
		dto.setDistrictName(job.getDistrict() != null ? job.getDistrict().getName() : null);
		if (job.getClimateLocation() != null) {
			ClimateLocation location = job.getClimateLocation();
			dto.setLatitude(location.getLatitude());
			dto.setLongitude(location.getLongitude());
			PlaceNames places = names.get(location.getId());
			if (places != null) {
				List<String> placeNames = new ArrayList<>(places.getDistricts());
				placeNames.addAll(places.getEnvironments());
				dto.setPlaceNames(placeNames);
			}
		}
		dto.setIncludeEnvironments(job.isIncludeEnvironments());
		dto.setFromDate(job.getFromDate());
		dto.setToDate(job.getToDate());
		dto.setLocationsTotal(job.getLocationsTotal());
		dto.setLocationsDone(job.getLocationsDone());
		dto.setLocationsFailed(job.getLocationsFailed());
		dto.setEstimatedCalls(job.getEstimatedCalls());
		dto.setCallsMade(job.getCallsMade());
		dto.setRowsAdded(job.getRowsAdded());
		dto.setRowsUpdated(job.getRowsUpdated());
		dto.setRowsSkipped(job.getRowsSkipped());
		dto.setLastError(job.getLastError());
		dto.setStartedBy(job.getStartedBy() != null ? job.getStartedBy().getUserName() : null);
		dto.setCreationDate(job.getCreationDate());
		dto.setStartedAt(job.getStartedAt());
		dto.setFinishedAt(job.getFinishedAt());
		return dto;
	}

	private void checkAvailable(boolean sourceIsOpenWeather, String offMessage) {

		if (!climateSettings.isClimateEnabled()) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateFeatureOff));
		}
		if (!sourceIsOpenWeather) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(offMessage));
		}
		if (climateSettings.getApiKey() == null) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateNoApiKey));
		}
	}

	private static LocalDate backfillStart(int years) {

		if (years < 1 || years > ClimateConfiguration.MAX_HISTORY_YEARS) {
			throw new ValidationRuntimeException(
				I18nProperties.getValidationError(Validations.climateBackfillYears, ClimateConfiguration.MAX_HISTORY_YEARS));
		}
		return LocalDate.of(LocalDate.now().getYear() - years, 1, 1);
	}

	private static void checkFetchPeriod(LocalDate from, LocalDate to) {
		if (from == null || to == null || from.isAfter(to) || !to.isBefore(LocalDate.now())) {
			throw new ValidationRuntimeException(I18nProperties.getValidationError(Validations.climateFetchPeriod));
		}
	}

	private ClimateJob buildJob(ClimateJobType type, RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments) {

		ClimateJob job = new ClimateJob();
		job.setJobType(type);
		job.setStatus(ClimateJobStatus.QUEUED);
		District scopeDistrict = district != null ? districtService.getByReferenceDto(district) : null;
		Region scopeRegion = scopeDistrict != null ? scopeDistrict.getRegion() : region != null ? regionService.getByReferenceDto(region) : null;
		job.setRegion(scopeRegion);
		job.setDistrict(scopeDistrict);
		job.setIncludeEnvironments(includeEnvironments);
		job.setStartedBy(userService.getCurrentUser());
		return job;
	}

	/**
	 * The locations a job with this scope covers, as the processor works them out.
	 */
	private List<Long> getScopeLocations(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments) {

		Long districtId = district != null ? districtService.getByReferenceDto(district).getId() : null;
		Long regionId = districtId == null && region != null ? regionService.getByReferenceDto(region).getId() : null;
		List<Long> locations = new ArrayList<>(climateLocationService.getDistrictLocationIds(regionId, districtId));
		if (includeEnvironments) {
			HashSet<Long> districtLocations = new HashSet<>(locations);
			climateLocationService.getEnvironmentLocationIds(regionId, districtId)
				.stream()
				.filter(id -> !districtLocations.contains(id))
				.forEach(locations::add);
		}
		return locations;
	}

	@LocalBean
	@Stateless
	public static class ClimateJobFacadeEjbLocal extends ClimateJobFacadeEjb {
	}
}
