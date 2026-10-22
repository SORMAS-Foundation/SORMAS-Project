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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateConfiguration;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationCategoryReferenceDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueDto;
import de.symeda.sormas.api.systemconfiguration.SystemConfigurationValueFacade;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateJobFacadeEjb.ClimateJobFacadeEjbLocal;
import de.symeda.sormas.backend.climate.ClimateProviderException.Reason;
import de.symeda.sormas.backend.infrastructure.district.District;

class ClimateJobProcessorTest extends AbstractBeanTest {

	private FakeProvider provider;
	private TestDataCreator.RDCF rdcf;
	private UserDto user;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		setClimateSetting(ClimateConfiguration.OPENWEATHER_API_KEY, "test-key");
		provider = new FakeProvider();
		getBean(ClimateJobProcessor.class).useForTests(key -> provider, Clock.systemDefaultZone());
		rdcf = creator.createRDCF();
		user = creator.createSurveillanceSupervisor(rdcf);
	}

	@Test
	void testNightlyFetchStoresYesterdayAndTheCatchUpDaysForEveryLocationInUse() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		createEnvironment(6.61, 3.34); // same cell as the district: fetched once
		createEnvironment(7.50, 3.35);

		runNight();

		ClimateJob job = getJobs(ClimateJobType.DAILY_FETCH).get(0);
		assertEquals(ClimateJobStatus.DONE, job.getStatus());
		assertEquals(2, job.getLocationsTotal());
		assertEquals(2, job.getLocationsDone());
		assertEquals(2, job.getCallsMade());
		assertEquals(20, job.getRowsAdded());

		LocalDate today = LocalDate.now();
		assertEquals(2, provider.calls.size());
		assertEquals(6.6, provider.calls.get(0).latitude, 0.0001, "district locations come first");
		assertEquals(today.minusDays(10), provider.calls.get(0).firstDay, "yesterday and the 9 days before it");

		List<ClimateObservation> rows = getObservations(getDistrictLocationId(rdcf.district.getUuid()));
		assertEquals(10, rows.size());
		assertEquals(ClimateDataSource.OPENWEATHER, rows.get(0).getSource());
		assertEquals(HumidityMeasure.NOON_READING, rows.get(0).getHumidityMeasure());
		assertEquals(today.minusDays(1), ClimateCalendar.toLocalDate(rows.get(9).getObservationDate()));
	}

	@Test
	void testRefetchWritesOnlyChangedValuesAndNeverTouchesImportedOrManualRows() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		Long location = getDistrictLocationId(rdcf.district.getUuid());
		LocalDate today = LocalDate.now();
		createObservation(location, today.minusDays(3), ClimateDataSource.IMPORT, 99.0);
		createObservation(location, today.minusDays(4), ClimateDataSource.MANUAL, 88.0);

		runNight();
		Map<LocalDate, ClimateObservation> before = getObservationsByDay(location);

		// OpenWeather revises one day; the others come back unchanged
		provider.rainfallByDay.put(today.minusDays(2), 12.0);
		runNight();

		ClimateJob second = getJobs(ClimateJobType.DAILY_FETCH).get(1);
		assertEquals(0, second.getRowsAdded());
		assertEquals(1, second.getRowsUpdated());

		Map<LocalDate, ClimateObservation> after = getObservationsByDay(location);
		assertEquals(12.0, after.get(today.minusDays(2)).getRainfall());
		assertEquals(
			before.get(today.minusDays(5)).getChangeDate(),
			after.get(today.minusDays(5)).getChangeDate(),
			"an unchanged day is not written again");
		assertEquals(99.0, after.get(today.minusDays(3)).getRainfall());
		assertEquals(ClimateDataSource.IMPORT, after.get(today.minusDays(3)).getSource());
		assertEquals(88.0, after.get(today.minusDays(4)).getRainfall());
	}

	@Test
	void testNightlyFetchStopsAtTheDailyLimit() {

		createEnvironment(6.60, 3.35);
		createEnvironment(7.50, 3.35);
		createEnvironment(8.50, 3.35);
		provider.successfulCallsBeforeLimit = 1;

		runNight();

		ClimateJob job = getJobs(ClimateJobType.DAILY_FETCH).get(0);
		assertEquals(ClimateJobStatus.STOPPED_AT_LIMIT, job.getStatus());
		assertEquals(1, job.getLocationsDone());
		assertEquals(3, job.getCallsMade(), "the refused call and its retry count too");
		assertEquals("OpenWeather's daily limit was reached after 3 calls; 2 locations were not fetched", job.getLastError());
	}

	@Test
	void testLocationsUsedOnlyByEnvironmentsTakeTurnsWhenTheLimitIsShort() {

		createEnvironment(6.60, 3.35);
		createEnvironment(7.50, 3.35);
		createEnvironment(8.50, 3.35);
		provider.successfulCallsBeforeLimit = 2;

		runNight();
		assertEquals(ClimateJobStatus.STOPPED_AT_LIMIT, getJobs(ClimateJobType.DAILY_FETCH).get(0).getStatus());
		assertEquals(7.5, provider.calls.get(1).latitude, 0.0001);

		provider.calls.clear();
		provider.successfulCallsBeforeLimit = Integer.MAX_VALUE;
		runNight();

		assertEquals(8.5, provider.calls.get(0).latitude, 0.0001, "the next night starts with the location that was not fetched");
		assertEquals(6.6, provider.calls.get(1).latitude, 0.0001);
		assertEquals(7.5, provider.calls.get(2).latitude, 0.0001);
	}

	@Test
	void testNightlyFetchFailsVisiblyWithoutAnApiKey() {

		setClimateSetting(ClimateConfiguration.OPENWEATHER_API_KEY, "");
		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);

		runNight();

		ClimateJob job = getJobs(ClimateJobType.DAILY_FETCH).get(0);
		assertEquals(ClimateJobStatus.FAILED, job.getStatus());
		assertEquals("No OpenWeather API key is set in System Configuration", job.getLastError());
		assertTrue(provider.calls.isEmpty());
	}

	@Test
	void testNothingIsFetchedWhenDailyDataIsExternal() {

		setClimateSetting(ClimateConfiguration.DAILY_SOURCE, "EXTERNAL");
		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);

		runNight();

		assertTrue(getJobs(ClimateJobType.DAILY_FETCH).isEmpty());
		assertTrue(provider.calls.isEmpty());
	}

	@Test
	void testRefusedKeyFailsTheJob() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		provider.failWith = Reason.ACCESS_DENIED;

		runNight();

		ClimateJob job = getJobs(ClimateJobType.DAILY_FETCH).get(0);
		assertEquals(ClimateJobStatus.FAILED, job.getStatus());
		assertEquals("refused: ACCESS_DENIED", job.getLastError());
	}

	@Test
	void testRefusedLocationIsSkippedAndTheJobCarriesOn() {

		createEnvironment(6.60, 3.35);
		createEnvironment(7.50, 3.35);
		provider.refusedLatitude = 6.6;

		runNight();

		ClimateJob job = getJobs(ClimateJobType.DAILY_FETCH).get(0);
		assertEquals(ClimateJobStatus.DONE, job.getStatus());
		assertEquals(1, job.getLocationsDone());
		assertEquals(1, job.getLocationsFailed());
		assertEquals("refused: LOCATION_REFUSED", job.getLastError());
	}

	@Test
	void testBackfillRunsOnlyAfterTonightsFetchAndSkipsDaysAlreadyHeld() {

		setClimateSetting(ClimateConfiguration.HISTORY_SOURCE, "OPENWEATHER");
		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		Long location = getDistrictLocationId(rdcf.district.getUuid());
		LocalDate firstDay = LocalDate.of(LocalDate.now().getYear() - 1, 1, 1);
		for (int i = 0; i < 10; i++) {
			createObservation(location, firstDay.plusDays(i), ClimateDataSource.IMPORT, 1.0);
		}

		ClimateJobFacadeEjbLocal facade = getBean(ClimateJobFacadeEjbLocal.class);
		long estimate = facade.estimateBackfillCalls(null, rdcf.district, false, 1);
		String uuid = facade.startBackfill(null, rdcf.district, false, 1, estimate);

		getBean(ClimateJobProcessor.class).processJobs();
		assertTrue(provider.calls.isEmpty(), "no backfill before tonight's fetch has run");

		runNight();

		ClimateJob backfill = getJob(uuid);
		assertEquals(ClimateJobStatus.DONE, backfill.getStatus());
		assertEquals(Integer.valueOf((int) estimate), backfill.getEstimatedCalls());
		List<LocalDate> backfillCalls = provider.calls.stream().skip(1).map(c -> c.firstDay).collect(Collectors.toList());
		assertEquals(firstDay.plusDays(10), backfillCalls.get(0), "imported days are not paid for again");
		assertEquals(estimate - 1, backfillCalls.size(), "the nightly fetch already brought the last 10 days");
		assertEquals(backfill.getCallsMade(), backfillCalls.size());
	}

	@Test
	void testBackfillPausesAtTheLimitAndResumesAfterTheLimitHasReset() {

		setClimateSetting(ClimateConfiguration.HISTORY_SOURCE, "OPENWEATHER");
		setClimateSetting(ClimateConfiguration.DAILY_SOURCE, "EXTERNAL");
		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		String uuid = getBean(ClimateJobFacadeEjbLocal.class).startBackfill(null, rdcf.district, false, 1, 1);
		provider.successfulCallsBeforeLimit = 3;

		getBean(ClimateJobProcessor.class).processJobs();

		ClimateJob paused = getJob(uuid);
		assertEquals(ClimateJobStatus.PAUSED_AT_LIMIT, paused.getStatus());
		assertNotNull(paused.getPausedAt());
		LocalDate refusedWindow = LocalDate.of(LocalDate.now().getYear() - 1, 1, 1).plusDays(30);
		assertEquals(refusedWindow, ClimateCalendar.toLocalDate(paused.getCursorDate()));

		provider.successfulCallsBeforeLimit = Integer.MAX_VALUE;
		getBean(ClimateJobProcessor.class).processJobs();
		assertEquals(3, provider.calls.size(), "still the same UTC day: the limit has not reset");

		executeInTransaction(em -> {
			em.createQuery("UPDATE climatejob j SET j.pausedAt = :yesterday WHERE j.uuid = :uuid")
				.setParameter("yesterday", new Date(System.currentTimeMillis() - 26L * 60 * 60 * 1000))
				.setParameter("uuid", uuid)
				.executeUpdate();
		});
		getBean(ClimateJobProcessor.class).processJobs();

		assertEquals(refusedWindow, provider.calls.get(3).firstDay, "carries on with the refused call");
		assertEquals(ClimateJobStatus.DONE, getJob(uuid).getStatus());
	}

	@Test
	void testTopUpQueuedForANewLocationWhenHistoryComesFromOpenWeather() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		LocalDate earliest = LocalDate.now().minusYears(2);
		createObservation(getDistrictLocationId(rdcf.district.getUuid()), earliest, ClimateDataSource.IMPORT, 1.0);

		createEnvironment(7.50, 3.35);
		assertTrue(getJobs(ClimateJobType.TOP_UP).isEmpty(), "no top-up while history does not come from OpenWeather");

		setClimateSetting(ClimateConfiguration.HISTORY_SOURCE, "OPENWEATHER");
		createEnvironment(8.50, 3.35);
		createEnvironment(8.51, 3.34); // same cell: no second top-up

		List<ClimateJob> topUps = getJobs(ClimateJobType.TOP_UP);
		assertEquals(1, topUps.size());
		assertEquals(ClimateJobStatus.QUEUED, topUps.get(0).getStatus());
		assertEquals(earliest, ClimateCalendar.toLocalDate(topUps.get(0).getFromDate()));
		assertEquals(LocalDate.now().minusDays(1), ClimateCalendar.toLocalDate(topUps.get(0).getToDate()));
	}

	@Test
	void testGridSizeChangeRelinksAtNightWithoutTopUps() {

		setClimateSetting(ClimateConfiguration.HISTORY_SOURCE, "OPENWEATHER");
		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		Long oldLocation = getDistrictLocationId(rdcf.district.getUuid());
		createObservation(oldLocation, LocalDate.now().minusYears(1), ClimateDataSource.IMPORT, 1.0);
		createEnvironment(7.50, 3.35);
		int topUpsBefore = getJobs(ClimateJobType.TOP_UP).size();

		setClimateSetting(ClimateConfiguration.GRID_SIZE, "0.1");
		runNight();

		ClimateLocation relinked = getBean(ClimateLocationService.class).getById(getDistrictLocationId(rdcf.district.getUuid()));
		assertEquals(0.1, relinked.getGridSize());
		assertEquals(66, relinked.getLatIndex());
		assertEquals(1, getObservations(relinked.getId()).stream().filter(o -> o.getSource() == ClimateDataSource.IMPORT).count(), "imported rows are copied");
		assertEquals(topUpsBefore, getJobs(ClimateJobType.TOP_UP).size(), "a grid size change queues no top-ups");
	}

	@Test
	void testCleanupDeletesOnlyLocationsThatNothingRefersTo() {

		ClimateLocationService locations = getBean(ClimateLocationService.class);
		Long unused = locations.getOrCreate(1.0, 1.0, 0.05).getId();
		Long withData = locations.getOrCreate(2.0, 2.0, 0.05).getId();
		createObservation(withData, LocalDate.now().minusDays(1), ClimateDataSource.IMPORT, 1.0);
		EnvironmentDto deleted = createEnvironment(3.0, 3.0);
		executeInTransaction(em -> {
			em.createQuery("UPDATE environments e SET e.deleted = true WHERE e.uuid = :uuid").setParameter("uuid", deleted.getUuid()).executeUpdate();
		});

		assertEquals(1, locations.deleteUnreferenced());

		assertNull(locations.getById(unused));
		assertNotNull(locations.getById(withData), "a location with data is kept");
		assertNotNull(
			locations.getByCell(0.05, 60, 60),
			"a deleted environment can be restored, so its location is kept");
	}

	@Test
	void testFetchNowWritesOnlyTheChosenDays() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		LocalDate from = LocalDate.now().minusDays(30);
		LocalDate to = from.plusDays(2);
		ClimateJobFacadeEjbLocal facade = getBean(ClimateJobFacadeEjbLocal.class);
		assertEquals(1, facade.estimateFetchCalls(null, null, false, ClimateCalendar.toDate(from), ClimateCalendar.toDate(to)));

		facade.fetchNow(null, null, false, ClimateCalendar.toDate(from), ClimateCalendar.toDate(to));
		getBean(ClimateJobProcessor.class).processJobs();

		ClimateJob job = getJobs(ClimateJobType.FETCH).get(0);
		assertEquals(ClimateJobStatus.DONE, job.getStatus());
		assertEquals(3, job.getRowsAdded());
		assertEquals(3, getObservations(getDistrictLocationId(rdcf.district.getUuid())).size());
	}

	@Test
	void testJobsCannotBeStartedWhenTheSourceIsNotOpenWeather() {

		ClimateJobFacadeEjbLocal facade = getBean(ClimateJobFacadeEjbLocal.class);
		assertThrows(ValidationRuntimeException.class, () -> facade.startBackfill(null, null, false, 1, 1));

		setClimateSetting(ClimateConfiguration.HISTORY_SOURCE, "OPENWEATHER");
		assertThrows(ValidationRuntimeException.class, () -> facade.startBackfill(null, null, false, 11, 1));

		setClimateSetting(ClimateConfiguration.OPENWEATHER_API_KEY, "");
		assertThrows(ValidationRuntimeException.class, () -> facade.startBackfill(null, null, false, 1, 1));

		Date today = new Date();
		setClimateSetting(ClimateConfiguration.OPENWEATHER_API_KEY, "test-key");
		assertThrows(ValidationRuntimeException.class, () -> facade.fetchNow(null, null, false, today, today));
	}

	@Test
	void testCancelAndResume() {

		setClimateSetting(ClimateConfiguration.HISTORY_SOURCE, "OPENWEATHER");
		ClimateJobFacadeEjbLocal facade = getBean(ClimateJobFacadeEjbLocal.class);
		String uuid = facade.startBackfill(null, null, false, 1, 1);

		facade.cancel(uuid);
		assertEquals(ClimateJobStatus.CANCELLED, getJob(uuid).getStatus());
		assertThrows(ValidationRuntimeException.class, () -> facade.cancel(uuid));

		facade.resume(uuid);
		assertEquals(ClimateJobStatus.QUEUED, getJob(uuid).getStatus());
	}

	@Test
	void testWindowsSkipHeldDays() {

		LocalDate from = LocalDate.of(2025, 1, 1);
		LocalDate to = LocalDate.of(2025, 1, 31);
		java.util.Set<LocalDate> held = new java.util.HashSet<>();
		for (int i = 0; i < 12; i++) {
			held.add(from.plusDays(i));
		}

		List<LocalDate> starts = ClimateCalendar.windowStarts(from, to, held, 10);

		assertEquals(2, starts.size());
		assertEquals(LocalDate.of(2025, 1, 13), starts.get(0));
		assertEquals(LocalDate.of(2025, 1, 23), starts.get(1));
		assertFalse(ClimateCalendar.windowStarts(from, from.plusDays(11), held, 10).iterator().hasNext());
	}

	private void runNight() {
		getBean(ClimateNightlyRun.class).run();
		getBean(ClimateJobProcessor.class).processJobs();
	}

	private void setClimateSetting(String key, String value) {

		SystemConfigurationValueFacade facade = getBean(SystemConfigurationValueFacade.class);
		List<String> uuids = facade.getAllUuids();
		SystemConfigurationValueDto setting = uuids.isEmpty()
			? null
			: facade.getByUuids(uuids).stream().filter(v -> key.equals(v.getKey())).findFirst().orElse(null);
		if (setting == null) {
			setting = SystemConfigurationValueDto.build();
			setting.setKey(key);
			setting.setCategory(climateCategory());
			setting.setOptional(true);
			setting.setEncrypt(false);
		}
		setting.setValue(value);
		facade.save(setting);
	}

	private SystemConfigurationCategoryReferenceDto climateCategory() {

		SystemConfigurationCategoryReferenceDto existing =
			getSystemConfigurationCategoryFacade().getCategoryReferenceDtoByName(ClimateConfiguration.CATEGORY_NAME);
		if (existing != null) {
			return existing;
		}
		SystemConfigurationCategoryDto category = SystemConfigurationCategoryDto.build();
		category.setName(ClimateConfiguration.CATEGORY_NAME);
		category.setCaption("Climate");
		return getSystemConfigurationCategoryFacade().getReferenceByUuid(getSystemConfigurationCategoryFacade().save(category).getUuid());
	}

	private EnvironmentDto createEnvironment(double latitude, double longitude) {
		return creator.createEnvironment("Environment " + latitude, EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
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

	private Long getDistrictLocationId(String districtUuid) {
		return executeInTransaction(em -> {
			District district = getDistrictService().getByUuid(districtUuid);
			return district.getClimateLocation().getId();
		});
	}

	private void createObservation(Long locationId, LocalDate day, ClimateDataSource source, double rainfall) {
		executeInTransaction(em -> {
			ClimateObservation observation = new ClimateObservation();
			observation.setClimateLocation(em.find(ClimateLocation.class, locationId));
			observation.setObservationDate(ClimateCalendar.toDate(day));
			observation.setSource(source);
			observation.setRainfall(rainfall);
			em.persist(observation);
		});
	}

	private List<ClimateObservation> getObservations(Long locationId) {
		return executeInTransaction(em -> {
			return getBean(ClimateObservationService.class).getByLocation(em.find(ClimateLocation.class, locationId));
		});
	}

	private Map<LocalDate, ClimateObservation> getObservationsByDay(Long locationId) {
		return getObservations(locationId).stream().collect(Collectors.toMap(o -> ClimateCalendar.toLocalDate(o.getObservationDate()), o -> o));
	}

	private List<ClimateJob> getJobs(ClimateJobType type) {
		return executeInTransaction(em -> {
			return em.createQuery("SELECT j FROM climatejob j WHERE j.jobType = :type ORDER BY j.creationDate, j.id", ClimateJob.class)
				.setParameter("type", type)
				.getResultList();
		});
	}

	private ClimateJob getJob(String uuid) {
		return getBean(ClimateJobService.class).getByUuid(uuid);
	}

	/**
	 * Returns 10 days from the first day asked for, up to yesterday, and records each call.
	 */
	private static class FakeProvider implements ClimateDataProvider {

		private static class Call {

			private final double latitude;
			private final LocalDate firstDay;

			Call(double latitude, LocalDate firstDay) {
				this.latitude = latitude;
				this.firstDay = firstDay;
			}
		}

		private final List<Call> calls = new ArrayList<>();
		private final Map<LocalDate, Double> rainfallByDay = new java.util.HashMap<>();
		private int successfulCallsBeforeLimit = Integer.MAX_VALUE;
		private Reason failWith;
		private Double refusedLatitude;

		@Override
		public ClimateDataSource source() {
			return ClimateDataSource.OPENWEATHER;
		}

		@Override
		public int maxDaysPerCall() {
			return 10;
		}

		@Override
		public ClimateFetchResult fetchWindow(double latitude, double longitude, LocalDate firstDay) throws ClimateProviderException {

			if (calls.size() >= successfulCallsBeforeLimit) {
				throw new ClimateProviderException(Reason.LIMIT_REACHED, "limit", 2);
			}
			if (failWith != null) {
				throw new ClimateProviderException(failWith, "refused: " + failWith, 1);
			}
			if (refusedLatitude != null && Math.abs(refusedLatitude - latitude) < 0.0001) {
				throw new ClimateProviderException(Reason.LOCATION_REFUSED, "refused: " + Reason.LOCATION_REFUSED, 1);
			}
			calls.add(new Call(latitude, firstDay));

			List<ClimateDay> days = new ArrayList<>();
			LocalDate today = LocalDate.now();
			for (LocalDate day = firstDay; day.isBefore(firstDay.plusDays(10)) && day.isBefore(today); day = day.plusDays(1)) {
				days.add(new ClimateDay(day, 20.0, 30.0, 25.0, rainfallByDay.getOrDefault(day, 5.0), 70.0, HumidityMeasure.NOON_READING));
			}
			return new ClimateFetchResult(days, 1);
		}

		@Override
		public void close() {
		}
	}
}
