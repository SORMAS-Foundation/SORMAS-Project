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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.climate.ClimateDailySource;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateJobCriteria;
import de.symeda.sormas.api.climate.ClimateJobFacade;
import de.symeda.sormas.api.climate.ClimateJobIndexDto;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationFacade;
import de.symeda.sormas.api.climate.ClimateRelinkDayDto;
import de.symeda.sormas.api.climate.ClimateSummaryDto;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateJobFacadeEjb.ClimateJobFacadeEjbLocal;
import de.symeda.sormas.backend.climate.ClimateObservationFacadeEjb.ClimateObservationFacadeEjbLocal;

/**
 * The job list, call summary and relink details behind the Climate data screen.
 */
class ClimateJobListTest extends AbstractBeanTest {

	private static final LocalDate DAY = LocalDate.now().minusDays(30);

	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private ClimateJobFacade jobFacade;
	private ClimateObservationFacade observationFacade;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		user = creator.createSurveillanceSupervisor(rdcf);
		jobFacade = getBean(ClimateJobFacadeEjbLocal.class);
		observationFacade = getBean(ClimateObservationFacadeEjbLocal.class);
	}

	@Test
	void testSummaryCountsTheCallsNeededPerNightAndLastNightsFetch() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		createEnvironment("Same cell", 6.61, 3.34);
		createEnvironment("Own cell", 7.50, 3.35);
		creator.createDistrict("Without point", getRegionService().getByUuid(rdcf.region.getUuid()));
		createJob(ClimateJobType.DAILY_FETCH, ClimateJobStatus.STOPPED_AT_LIMIT, 1, "OpenWeather's daily limit was reached");

		ClimateSummaryDto summary = jobFacade.getSummary();
		assertEquals(ClimateDailySource.OPENWEATHER, summary.getDailySource());
		assertFalse(summary.isApiKeySet());
		assertEquals(1, summary.getDistrictLocations());
		assertEquals(1, summary.getEnvironmentOnlyLocations(), "an environment in a district's cell costs no extra call");
		assertEquals(2, summary.getCallsPerNight());
		assertEquals(1, summary.getDistrictsWithoutPoint());
		assertEquals(5, summary.getHistoryYears());
		assertEquals(3, summary.getMinHistoryYears());
		assertEquals(ClimateJobStatus.STOPPED_AT_LIMIT, summary.getLastNightlyFetch().getStatus());
		assertEquals(1, summary.getLastNightlyFetch().getCallsMade());

		setClimateSetting(ClimateConfiguration.OPENWEATHER_API_KEY, "test-key");
		assertTrue(jobFacade.getSummary().isApiKeySet());
	}

	@Test
	void testJobListFilters() {

		createJob(ClimateJobType.DAILY_FETCH, ClimateJobStatus.DONE, 2, null);
		createJob(ClimateJobType.BACKFILL, ClimateJobStatus.PAUSED_AT_LIMIT, 40, "limit");
		createJob(ClimateJobType.TOP_UP, ClimateJobStatus.FAILED, 3, "error");

		assertEquals(
			Arrays.asList(ClimateJobType.TOP_UP, ClimateJobType.BACKFILL, ClimateJobType.DAILY_FETCH),
			types(new ClimateJobCriteria()),
			"newest first");
		assertEquals(Collections.singletonList(ClimateJobType.BACKFILL), types(new ClimateJobCriteria().jobType(ClimateJobType.BACKFILL)));
		assertEquals(Collections.singletonList(ClimateJobType.TOP_UP), types(new ClimateJobCriteria().status(ClimateJobStatus.FAILED)));
		assertEquals(
			Collections.singletonList(ClimateJobType.BACKFILL),
			types(new ClimateJobCriteria().createdFrom(new Date(System.currentTimeMillis() + 86_400_000L))),
			"open jobs are listed whatever their age");
		assertEquals(3, jobFacade.count(new ClimateJobCriteria()));

		ClimateJobIndexDto backfill = jobFacade.getIndexList(new ClimateJobCriteria().jobType(ClimateJobType.BACKFILL), 0, 10, null).get(0);
		assertTrue(backfill.isCancellable());
		assertTrue(backfill.isResumable());
		assertEquals(user.getUserName(), backfill.getStartedBy());
	}

	@Test
	void testRelinkDaysShowBothValuesUntilCorrected() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		EnvironmentDto environment = createEnvironment("Well A", 7.50, 3.35);
		importForDistrict(DAY, 10.0);
		importForDistrict(DAY.minusDays(1), 11.0);
		importForEnvironment(environment.getUuid(), DAY, 20.0);

		saveDistrictClimatePoint(rdcf.district.getUuid(), 7.50, 3.35);

		List<ClimateJobIndexDto> relinks = jobFacade.getIndexList(new ClimateJobCriteria().onlyRelinksWithSkippedDays(true), 0, 10, null);
		assertEquals(1, relinks.size());
		ClimateJobIndexDto relink = relinks.get(0);
		assertEquals(rdcf.district.getCaption(), relink.getDistrictName());
		assertEquals(1, relink.getRowsAdded(), "the day the cell had no data was copied");
		assertEquals(1, relink.getRowsSkipped());

		List<ClimateRelinkDayDto> days = jobFacade.getRelinkDays(relink.getUuid());
		assertEquals(1, days.size(), "the copied day is not listed");
		ClimateRelinkDayDto day = days.get(0);
		assertEquals(10.0, day.getDistrictValue().getRainfall());
		assertEquals(20.0, day.getKeptValue().getRainfall());
		assertEquals(Collections.singletonList("Well A"), day.getKeptValue().getEnvironmentNames());
		assertFalse(day.isSameValues());

		ClimateObservationEntryDto correction = day.getDistrictValue().toEntry();
		correction.setClimateLocationUuid(day.getKeptValue().getClimateLocationUuid());
		observationFacade.saveManualEntry(correction);

		ClimateRelinkDayDto corrected = jobFacade.getRelinkDays(relink.getUuid()).get(0);
		assertTrue(corrected.isSameValues(), "a corrected day stays listed, now with the same values");
		assertEquals(ClimateDataSource.MANUAL, corrected.getKeptValue().getSource());
	}

	@Test
	void testRelinkDaysOfOtherJobsAreEmpty() {
		assertTrue(jobFacade.getRelinkDays(createJob(ClimateJobType.BACKFILL, ClimateJobStatus.DONE, 1, null)).isEmpty());
		assertTrue(jobFacade.getRelinkDays("unknown").isEmpty());
	}

	private List<ClimateJobType> types(ClimateJobCriteria criteria) {
		return jobFacade.getIndexList(criteria, 0, 100, null).stream().map(ClimateJobIndexDto::getJobType).collect(Collectors.toList());
	}

	private String createJob(ClimateJobType type, ClimateJobStatus status, int callsMade, String lastError) {
		return executeInTransaction(em -> {
			ClimateJob job = new ClimateJob();
			job.setJobType(type);
			job.setStatus(status);
			job.setCallsMade(callsMade);
			job.setLastError(lastError);
			job.setStartedBy(getUserService().getByUuid(user.getUuid()));
			getBean(ClimateJobService.class).ensurePersisted(job);
			// a distinct creation time per job, so newest-first is well defined
			sleepOneMillisecond();
			return job.getUuid();
		});
	}

	private static void sleepOneMillisecond() {
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void importForDistrict(LocalDate day, double rainfall) {
		ClimateObservationEntryDto entry = entry(day, rainfall);
		entry.setDistrictUuid(rdcf.district.getUuid());
		observationFacade.importEntries(Collections.singletonList(entry), false, new Date());
	}

	private void importForEnvironment(String environmentUuid, LocalDate day, double rainfall) {
		ClimateObservationEntryDto entry = entry(day, rainfall);
		entry.setEnvironmentUuid(environmentUuid);
		observationFacade.importEntries(Collections.singletonList(entry), false, new Date());
	}

	private static ClimateObservationEntryDto entry(LocalDate day, double rainfall) {
		ClimateObservationEntryDto entry = new ClimateObservationEntryDto();
		entry.setDate(day.toString());
		entry.setRainfall(rainfall);
		entry.setRelativeHumidity(80.0);
		entry.setHumidityMeasure(HumidityMeasure.DAILY_MEAN);
		return entry;
	}

	private EnvironmentDto createEnvironment(String name, double latitude, double longitude) {
		return creator.createEnvironment(name, EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(latitude);
			e.getLocation().setLongitude(longitude);
		});
	}

	private void saveDistrictClimatePoint(String districtUuid, double latitude, double longitude) {
		DistrictDto district = getDistrictFacade().getByUuid(districtUuid);
		district.setClimateLatitude(latitude);
		district.setClimateLongitude(longitude);
		getDistrictFacade().save(district);
	}

	private void setClimateSetting(String key, String value) {

		SystemConfigurationValueFacade facade = getBean(SystemConfigurationValueFacade.class);
		List<String> uuids = facade.getAllUuids();
		SystemConfigurationValueDto setting =
			uuids.isEmpty() ? null : facade.getByUuids(uuids).stream().filter(v -> key.equals(v.getKey())).findFirst().orElse(null);
		if (setting == null) {
			setting = SystemConfigurationValueDto.build();
			setting.setKey(key);
			SystemConfigurationCategoryDto category = SystemConfigurationCategoryDto.build();
			category.setName(ClimateConfiguration.CATEGORY_NAME);
			category.setCaption("Climate");
			setting.setCategory(
				getSystemConfigurationCategoryFacade().getReferenceByUuid(getSystemConfigurationCategoryFacade().save(category).getUuid()));
			setting.setOptional(true);
			setting.setEncrypt(false);
		}
		setting.setValue(value);
		facade.save(setting);
	}
}
