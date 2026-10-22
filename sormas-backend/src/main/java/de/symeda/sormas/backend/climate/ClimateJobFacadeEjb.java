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
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.climate.ClimateCosts;
import de.symeda.sormas.api.climate.ClimateJobFacade;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
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
	public long estimateBackfillCalls(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments, int years) {

		LocalDate from = backfillStart(years);
		LocalDate to = LocalDate.now().minusDays(1);
		long calls = 0;
		for (Long locationId : getScopeLocations(region, district, includeEnvironments)) {
			calls += ClimateCalendar
				.windowStarts(from, to, climateObservationService.getHeldDays(locationId, from, to), ClimateCosts.OPENWEATHER_DAYS_PER_CALL)
				.size();
		}
		return calls;
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
	public long estimateFetchCalls(RegionReferenceDto region, DistrictReferenceDto district, boolean includeEnvironments, Date from, Date to) {

		LocalDate first = ClimateCalendar.toLocalDate(from);
		LocalDate last = ClimateCalendar.toLocalDate(to);
		checkFetchPeriod(first, last);
		long days = last.toEpochDay() - first.toEpochDay() + 1;
		return getScopeLocations(region, district, includeEnvironments).size() * ClimateCosts.callsPerLocation(days);
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
	public void resume(String jobUuid) {

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
