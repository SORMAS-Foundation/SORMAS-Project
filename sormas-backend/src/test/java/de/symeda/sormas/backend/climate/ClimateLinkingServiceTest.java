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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.infrastructure.district.District;

class ClimateLinkingServiceTest extends AbstractBeanTest {

	@Test
	void testEnvironmentLinkedToItsGridCellOnSave() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		EnvironmentDto environment = createEnvironment(rdcf, user, 6.60, 3.35);

		ClimateLocation location = getEnvironmentClimateLocation(environment.getUuid());
		assertNotNull(location);
		assertEquals(0.05, location.getGridSize());
		assertEquals(132, location.getLatIndex());
		assertEquals(67, location.getLonIndex());
		assertEquals(6.6, location.getLatitude());
		assertEquals(3.35, location.getLongitude());
	}

	@Test
	void testEnvironmentsInTheSameCellShareOneLocation() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		EnvironmentDto first = createEnvironment(rdcf, user, 6.601, 3.351);
		EnvironmentDto second = createEnvironment(rdcf, user, 6.61, 3.34);
		EnvironmentDto farAway = createEnvironment(rdcf, user, 6.70, 3.35);

		Long firstLocation = getEnvironmentClimateLocation(first.getUuid()).getId();
		assertEquals(firstLocation, getEnvironmentClimateLocation(second.getUuid()).getId());
		assertNotEquals(firstLocation, getEnvironmentClimateLocation(farAway.getUuid()).getId());
	}

	@Test
	void testEnvironmentNotLinkedWhileFeatureIsOff() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		EnvironmentDto environment = createEnvironment(rdcf, user, 6.60, 3.35);

		assertNull(getEnvironmentClimateLocation(environment.getUuid()));
	}

	@Test
	void testEnvironmentRelinkedWhenGpsMovesAndUnlinkedWhenGpsUnusable() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);

		EnvironmentDto environment = createEnvironment(rdcf, user, 6.60, 3.35);
		Long originalLocation = getEnvironmentClimateLocation(environment.getUuid()).getId();

		environment = getEnvironmentFacade().getEnvironmentByUuid(environment.getUuid());
		environment.getLocation().setLatitude(7.50);
		environment = getEnvironmentFacade().save(environment);
		ClimateLocation moved = getEnvironmentClimateLocation(environment.getUuid());
		assertNotEquals(originalLocation, moved.getId());
		assertEquals(150, moved.getLatIndex());

		environment.getLocation().setLatitude(0.0);
		environment.getLocation().setLongitude(0.0);
		getEnvironmentFacade().save(environment);
		assertNull(getEnvironmentClimateLocation(environment.getUuid()));
	}

	@Test
	void testDistrictWithClimatePointLinkedToItsCell() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);

		ClimateLocation location = getDistrictClimateLocation(rdcf.district.getUuid());
		assertTrue(location.hasCoordinates());
		assertEquals(132, location.getLatIndex());
		assertEquals(67, location.getLonIndex());
	}

	@Test
	void testDistrictWithoutPointGetsOwnLocationWithoutCoordinates() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();

		// no shapefiles are loaded in tests, so a district without a climate point has no point at all
		getDistrictFacade().save(getDistrictFacade().getByUuid(rdcf.district.getUuid()));
		ClimateLocation location = getDistrictClimateLocation(rdcf.district.getUuid());
		assertNotNull(location);
		assertFalse(location.hasCoordinates());

		// linking again keeps the same location
		assertEquals(0, getBean(ClimateLinkingService.class).linkAllDistricts());
		assertEquals(location.getId(), getDistrictClimateLocation(rdcf.district.getUuid()).getId());
	}

	@Test
	void testDistrictWithoutPointReusesItsLocationWhenTheCellIsFree() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		getDistrictFacade().save(getDistrictFacade().getByUuid(rdcf.district.getUuid()));
		ClimateLocation withoutCoordinates = getDistrictClimateLocation(rdcf.district.getUuid());
		assertFalse(withoutCoordinates.hasCoordinates());
		createObservation(withoutCoordinates, DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 1)), ClimateDataSource.IMPORT, 12.0);

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);

		ClimateLocation reused = getDistrictClimateLocation(rdcf.district.getUuid());
		assertEquals(withoutCoordinates.getId(), reused.getId(), "the same location is given the cell, no new one is created");
		assertTrue(reused.hasCoordinates());
		assertEquals(132, reused.getLatIndex());
		assertEquals(67, reused.getLonIndex());
		assertEquals(1, getObservationsByDay(reused).size(), "imported rows stay where they are");
		assertTrue(getRelinkJobs().isEmpty(), "nothing moved, so no relink entry");
	}

	@Test
	void testDistrictWithoutPointJoinsACellThatAlreadyHasALocation() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		EnvironmentDto environment = createEnvironment(rdcf, user, 6.60, 3.35);
		ClimateLocation environmentLocation = getEnvironmentClimateLocation(environment.getUuid());

		getDistrictFacade().save(getDistrictFacade().getByUuid(rdcf.district.getUuid()));
		ClimateLocation withoutCoordinates = getDistrictClimateLocation(rdcf.district.getUuid());
		Date day = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 1));
		createObservation(withoutCoordinates, day, ClimateDataSource.IMPORT, 12.0);

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.61, 3.34);

		assertEquals(
			environmentLocation.getId(),
			getDistrictClimateLocation(rdcf.district.getUuid()).getId(),
			"the district joins the cell's location");
		assertEquals(12.0, getObservationsByDay(environmentLocation).get(day.getTime()).getRainfall(), "imported rows are copied across");
		assertFalse(getLocation(withoutCoordinates.getId()).hasCoordinates(), "the old location is left unchanged");
	}

	@Test
	void testLinkAllDistrictsLinksDistrictsCreatedWhileFeatureWasOff() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		assertNull(getDistrictClimateLocation(rdcf.district.getUuid()));

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		assertTrue(getBean(ClimateLinkingService.class).linkAllDistricts() >= 1);

		assertNotNull(getDistrictClimateLocation(rdcf.district.getUuid()));
	}

	@Test
	void testSwitchingTheFeatureOnLinksExistingDistrictsAndEnvironments() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		EnvironmentDto environment = createEnvironment(rdcf, user, 6.60, 3.35);
		assertNull(getDistrictClimateLocation(rdcf.district.getUuid()));
		assertNull(getEnvironmentClimateLocation(environment.getUuid()));

		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, true);

		assertNotNull(getDistrictClimateLocation(rdcf.district.getUuid()));
		assertNotNull(getEnvironmentClimateLocation(environment.getUuid()));
	}

	@Test
	void testDistrictImportTemplateFollowsTheFeatureSwitch() throws IOException {

		Path template = Paths.get(getImportFacade().getDistrictImportTemplateFilePath());

		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, true);
		assertTrue(Files.readString(template).contains(DistrictDto.CLIMATE_LATITUDE), "climate columns appear when the feature is switched on");

		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, false);
		assertFalse(Files.readString(template).contains(DistrictDto.CLIMATE_LATITUDE), "climate columns disappear when it is switched off");
	}

	@Test
	void testEnvironmentMoveCopiesImportedAndManualRows() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		EnvironmentDto environment = createEnvironment(rdcf, user, 6.60, 3.35);
		ClimateLocation oldLocation = getEnvironmentClimateLocation(environment.getUuid());

		Date day1 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 3));
		Date day2 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 2));
		Date day3 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 1));
		createObservation(oldLocation, day1, ClimateDataSource.IMPORT, 10.0);
		createObservation(oldLocation, day2, ClimateDataSource.MANUAL, 20.0);
		createObservation(oldLocation, day3, ClimateDataSource.OPENWEATHER, 30.0);
		ClimateLocation newLocation = getBean(ClimateLocationService.class).getOrCreate(7.50, 3.35, 0.05);
		createObservation(newLocation, day2, ClimateDataSource.MANUAL, 88.0);

		environment = getEnvironmentFacade().getEnvironmentByUuid(environment.getUuid());
		environment.getLocation().setLatitude(7.50);
		getEnvironmentFacade().save(environment);

		assertEquals(newLocation.getId(), getEnvironmentClimateLocation(environment.getUuid()).getId());
		Map<Long, ClimateObservation> newRows = getObservationsByDay(newLocation);
		assertEquals(2, newRows.size());
		assertEquals(ClimateDataSource.IMPORT, newRows.get(day1.getTime()).getSource(), "the site's imported value moves with it (decision 132)");
		assertEquals(10.0, newRows.get(day1.getTime()).getRainfall());
		assertEquals(88.0, newRows.get(day2.getTime()).getRainfall(), "the new cell's own manual value stays");
		assertNull(newRows.get(day3.getTime()), "OpenWeather rows are fetched again for the new point");
		assertEquals(3, getObservationsByDay(oldLocation).size(), "the old location keeps its rows");
		assertTrue(getRelinkJobs().isEmpty(), "environment moves are logged, not stored as relinks");
	}

	@Test
	void testDistrictPointChangeCopiesImportedAndManualRows() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		ClimateLocation oldLocation = getDistrictClimateLocation(rdcf.district.getUuid());

		Date day1 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 3));
		Date day2 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 2));
		Date day3 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 1));
		createObservation(oldLocation, day1, ClimateDataSource.IMPORT, 10.0);
		createObservation(oldLocation, day2, ClimateDataSource.MANUAL, 20.0);
		createObservation(oldLocation, day3, ClimateDataSource.OPENWEATHER, 30.0);

		// the new cell already has rows of its own: an API row the import outranks, and a manual row that must stay
		ClimateLocation newLocation = getBean(ClimateLocationService.class).getOrCreate(7.50, 3.35, 0.05);
		createObservation(newLocation, day1, ClimateDataSource.OPENWEATHER, 99.0);
		createObservation(newLocation, day2, ClimateDataSource.MANUAL, 88.0);

		saveDistrictClimatePoint(rdcf.district.getUuid(), 7.50, 3.35);

		assertEquals(newLocation.getId(), getDistrictClimateLocation(rdcf.district.getUuid()).getId());

		Map<Long, ClimateObservation> newRows = getObservationsByDay(newLocation);
		assertEquals(2, newRows.size());
		assertEquals(ClimateDataSource.IMPORT, newRows.get(day1.getTime()).getSource());
		assertEquals(10.0, newRows.get(day1.getTime()).getRainfall());
		assertEquals(88.0, newRows.get(day2.getTime()).getRainfall());
		assertNull(newRows.get(day3.getTime()), "OpenWeather rows belong to the old point and are not copied");

		assertEquals(3, getObservationsByDay(oldLocation).size(), "the old location keeps its rows");

		List<ClimateJob> relinks = getRelinkJobs();
		assertEquals(1, relinks.size(), "the move is stored once");
		ClimateJob relink = relinks.get(0);
		assertEquals(ClimateJobStatus.DONE, relink.getStatus());
		assertEquals(rdcf.district.getUuid(), relink.getDistrict().getUuid());
		assertEquals(oldLocation.getId(), relink.getFromClimateLocation().getId());
		assertEquals(newLocation.getId(), relink.getClimateLocation().getId());
		assertEquals(1, relink.getRowsAdded());
		assertEquals(1, relink.getRowsSkipped());
		assertEquals(day2.getTime(), relink.getFromDate().getTime());
		assertEquals(day2.getTime(), relink.getToDate().getTime());
		assertEquals(getCurrentUserService().getCurrentUser().getUuid(), relink.getStartedBy().getUuid());
		assertNotNull(relink.getStartedAt());
	}

	@Test
	void testMovesWithNothingCopiedAreNotStored() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		createObservation(
			getDistrictClimateLocation(rdcf.district.getUuid()),
			DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 1)),
			ClimateDataSource.OPENWEATHER,
			5.0);

		saveDistrictClimatePoint(rdcf.district.getUuid(), 7.50, 3.35);

		assertTrue(getRelinkJobs().isEmpty(), "OpenWeather rows are not copied, so the move is only logged by its link");
	}

	@Test
	void testCopyCountsCopiedAndSkippedDays() {

		ClimateLocationService locationService = getBean(ClimateLocationService.class);
		ClimateLocation from = locationService.getOrCreate(6.60, 3.35, 0.05);
		ClimateLocation to = locationService.getOrCreate(7.50, 3.35, 0.05);

		Date day1 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 4));
		Date day2 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 3));
		Date day3 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 2));
		Date day4 = DateHelper.getStartOfDay(DateHelper.subtractDays(new Date(), 1));
		createObservation(from, day1, ClimateDataSource.IMPORT, 1.0);
		createObservation(from, day2, ClimateDataSource.MANUAL, 2.0);
		createObservation(from, day3, ClimateDataSource.IMPORT, 3.0);
		createObservation(from, day4, ClimateDataSource.OPENWEATHER, 4.0);
		// ties: the target's own rows stay
		createObservation(to, day2, ClimateDataSource.MANUAL, 20.0);
		createObservation(to, day3, ClimateDataSource.IMPORT, 30.0);

		ClimateRowCopyResult result = executeInTransaction(em -> {
			return getBean(ClimateObservationService.class)
				.copyExternalRows(em.find(ClimateLocation.class, from.getId()), em.find(ClimateLocation.class, to.getId()));
		});

		assertEquals(1, result.getCopied());
		assertEquals(2, result.getSkipped());
		assertEquals(day2.getTime(), result.getFirstSkippedDay().getTime());
		assertEquals(day3.getTime(), result.getLastSkippedDay().getTime());

		Map<Long, ClimateObservation> targetRows = getObservationsByDay(to);
		assertEquals(1.0, targetRows.get(day1.getTime()).getRainfall());
		assertEquals(20.0, targetRows.get(day2.getTime()).getRainfall());
		assertEquals(30.0, targetRows.get(day3.getTime()).getRainfall());
		assertNull(targetRows.get(day4.getTime()));
	}

	@Test
	void testDistrictClimatePointValidation() {

		TestDataCreator.RDCF rdcf = creator.createRDCF();

		DistrictDto onlyLatitude = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		onlyLatitude.setClimateLatitude(6.6);
		assertThrows(ValidationRuntimeException.class, () -> getDistrictFacade().save(onlyLatitude));

		DistrictDto zeroPoint = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		zeroPoint.setClimateLatitude(0.0);
		zeroPoint.setClimateLongitude(0.0);
		assertThrows(ValidationRuntimeException.class, () -> getDistrictFacade().save(zeroPoint));
	}

	private EnvironmentDto createEnvironment(TestDataCreator.RDCF rdcf, UserDto user, double latitude, double longitude) {

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

	private ClimateLocation getEnvironmentClimateLocation(String environmentUuid) {
		return executeInTransaction(em -> {
			Environment environment = getEnvironmentService().getByUuid(environmentUuid);
			ClimateLocation location = environment.getClimateLocation();
			return location != null ? em.find(ClimateLocation.class, location.getId()) : null;
		});
	}

	private ClimateLocation getDistrictClimateLocation(String districtUuid) {
		return executeInTransaction(em -> {
			District district = getDistrictService().getByUuid(districtUuid);
			ClimateLocation location = district.getClimateLocation();
			return location != null ? em.find(ClimateLocation.class, location.getId()) : null;
		});
	}

	private ClimateLocation getLocation(Long id) {
		return executeInTransaction((EntityManager em) -> em.find(ClimateLocation.class, id));
	}

	private void createObservation(ClimateLocation location, Date day, ClimateDataSource source, double rainfall) {
		executeInTransaction(em -> {
			ClimateObservation observation = new ClimateObservation();
			observation.setClimateLocation(em.find(ClimateLocation.class, location.getId()));
			observation.setObservationDate(day);
			observation.setSource(source);
			observation.setRainfall(rainfall);
			em.persist(observation);
		});
	}

	private List<ClimateJob> getRelinkJobs() {
		return executeInTransaction(em -> {
			List<ClimateJob> jobs = em.createQuery("SELECT j FROM climatejob j WHERE j.jobType = :type", ClimateJob.class)
				.setParameter("type", ClimateJobType.RELINK)
				.getResultList();
			// touch the lazy links while the transaction is open
			jobs.forEach(j -> {
				j.getDistrict().getUuid();
				j.getFromClimateLocation().getId();
				j.getClimateLocation().getId();
				if (j.getStartedBy() != null) {
					j.getStartedBy().getUuid();
				}
			});
			return jobs;
		});
	}

	private Map<Long, ClimateObservation> getObservationsByDay(ClimateLocation location) {
		return executeInTransaction(em -> {
			List<ClimateObservation> rows = getBean(ClimateObservationService.class).getByLocation(em.find(ClimateLocation.class, location.getId()));
			return rows.stream().collect(Collectors.toMap(o -> o.getObservationDate().getTime(), o -> o));
		});
	}
}
