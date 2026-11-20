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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateDailyExportDto;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateEnvironmentContextDto;
import de.symeda.sormas.api.climate.ClimateJobStatus;
import de.symeda.sormas.api.climate.ClimateJobType;
import de.symeda.sormas.api.climate.ClimateObservationExportDto;
import de.symeda.sormas.api.climate.ClimateSeriesFacade;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateSeriesFacadeEjb.ClimateSeriesFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;

class ClimateEnvironmentContextTest extends AbstractBeanTest {

	private final LocalDate yesterday = LocalDate.now().minusDays(1);
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private ClimateSeriesFacade facade;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		user = useNationalAdminLogin();
		facade = getBean(ClimateSeriesFacadeEjbLocal.class);
		getBean(ClimateNormalsCache.class).clear();
	}

	@Test
	void testOwnPointSharedWithTheDistrictAndAnotherEnvironment() {

		EnvironmentDto environment = createEnvironment("Well A", 6.60, 3.35);
		createEnvironment("Well B", 6.61, 3.36);
		Long location = environmentLocation(environment.getUuid());
		assertEquals(location, districtLocation(6.60, 3.35), "same cell");
		fill(location, yesterday.minusDays(9), yesterday.minusDays(2));

		ClimateEnvironmentContextDto context = facade.getEnvironmentContext(environment.toReference());

		assertTrue(context.isOwnLocation());
		assertEquals(6.60, context.getLatitude());
		assertEquals(6.60, context.getCellLatitude(), 1e-9);
		assertEquals(3.35, context.getCellLongitude(), 1e-9);
		assertEquals(Collections.singletonList(rdcf.district.getCaption()), context.getSharedDistricts());
		assertEquals(Collections.singletonList("Well B"), context.getSharedEnvironments());
		assertTrue(context.isShared());
		assertEquals(yesterday.minusDays(2), context.getLatestDay());
		assertEquals(yesterday.minusDays(9), context.getHistoryFrom());
		assertTrue(context.isStillUpdated());
		assertFalse(context.isOutsideDistrict(), "no district shapes are loaded");
		assertFalse(context.isArchived());
		assertNull(context.getLoadingJobType());
	}

	@Test
	void testWithoutUsableGpsTheDistrictIsShown() {

		EnvironmentDto environment = createEnvironment("Well A", 6.80, 3.50);
		Long district = districtLocation(6.60, 3.35);
		fill(district, yesterday, yesterday);
		executeInTransaction(em -> {
			getEnvironmentService().getByUuid(environment.getUuid()).setClimateLocation(null);
		});

		ClimateEnvironmentContextDto context = facade.getEnvironmentContext(environment.toReference());

		assertFalse(context.isOwnLocation());
		assertEquals(rdcf.district, context.getDistrict());
		assertTrue(context.isDistrictHasPoint());
		assertEquals(6.60, context.getCellLatitude(), 1e-9);
		assertTrue(context.getSharedDistricts().isEmpty(), "sharing is only named for the environment's own location");
		assertEquals(yesterday, context.getLatestDay());
	}

	@Test
	void testArchivedEnvironmentIsStillUpdatedOnlyWhileShared() {

		EnvironmentDto environment = createEnvironment("Well A", 6.80, 3.50);
		executeInTransaction(em -> {
			getEnvironmentService().getByUuid(environment.getUuid()).setArchived(true);
		});

		ClimateEnvironmentContextDto alone = facade.getEnvironmentContext(environment.toReference());
		assertTrue(alone.isArchived());
		assertFalse(alone.isStillUpdated());

		districtLocation(6.80, 3.50);
		ClimateEnvironmentContextDto shared = facade.getEnvironmentContext(environment.toReference());
		assertTrue(shared.isStillUpdated());
		assertEquals(Collections.singletonList(rdcf.district.getCaption()), shared.getSharedDistricts());
	}

	@Test
	void testOpenHistoryJobs() {

		EnvironmentDto environment = createEnvironment("Well A", 6.80, 3.50);
		Long location = environmentLocation(environment.getUuid());

		Long backfill = queueJob(ClimateJobType.BACKFILL, null, false);
		assertNull(facade.getEnvironmentContext(environment.toReference()).getLoadingJobType(), "district locations only");

		setIncludeEnvironments(backfill);
		ClimateEnvironmentContextDto context = facade.getEnvironmentContext(environment.toReference());
		assertEquals(ClimateJobType.BACKFILL, context.getLoadingJobType());
		assertEquals(3, context.getLoadingLocationsDone());
		assertEquals(12, context.getLoadingLocationsTotal());

		finish(backfill);
		queueJob(ClimateJobType.TOP_UP, location, false);
		assertEquals(ClimateJobType.TOP_UP, facade.getEnvironmentContext(environment.toReference()).getLoadingJobType());
	}

	@Test
	void testExportRows() {

		EnvironmentDto environment = createEnvironment("Well A", 6.80, 3.50);
		Long location = environmentLocation(environment.getUuid());
		fill(location, yesterday.minusDays(1), yesterday.minusDays(1));

		List<ClimateDailyExportDto> rows = facade.getDailyExportList(environment.toReference(), yesterday.minusDays(1), yesterday);

		assertEquals(2, rows.size());
		ClimateDailyExportDto held = rows.get(0);
		assertEquals(5.0, held.getRainfall());
		assertEquals(HumidityMeasure.NOON_READING, held.getHumidityMeasure());
		assertEquals(ClimateDataSource.OPENWEATHER, held.getSource());
		assertEquals(ClimateObservationExportDto.OPENWEATHER_CREDIT, held.getCredit());
		assertNull(held.getRainfallP50(), "no history for normals");
		assertNull(held.getNormalsFrom());
		ClimateDailyExportDto gap = rows.get(1);
		assertNull(gap.getRainfall(), "a missing day is empty, never 0");
		assertNull(gap.getSource());
		assertNull(gap.getCredit());
	}

	private EnvironmentDto createEnvironment(String name, double latitude, double longitude) {
		return creator.createEnvironment(name, EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(latitude);
			e.getLocation().setLongitude(longitude);
		});
	}

	private Long environmentLocation(String environmentUuid) {
		return executeInTransaction(em -> {
			return getEnvironmentService().getByUuid(environmentUuid).getClimateLocation().getId();
		});
	}

	private Long districtLocation(double latitude, double longitude) {
		DistrictDto district = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		district.setClimateLatitude(latitude);
		district.setClimateLongitude(longitude);
		getDistrictFacade().save(district);
		return executeInTransaction(em -> {
			District entity = getDistrictService().getByUuid(rdcf.district.getUuid());
			if (entity.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(entity);
			}
			return entity.getClimateLocation().getId();
		});
	}

	private Long queueJob(ClimateJobType type, Long locationId, boolean includeEnvironments) {
		return executeInTransaction(em -> {
			ClimateJob job = new ClimateJob();
			job.setJobType(type);
			job.setStatus(ClimateJobStatus.RUNNING);
			job.setIncludeEnvironments(includeEnvironments);
			if (locationId != null) {
				job.setClimateLocation(em.find(ClimateLocation.class, locationId));
			}
			job.setLocationsTotal(12);
			job.setLocationsDone(3);
			em.persist(job);
			return job.getId();
		});
	}

	private void setIncludeEnvironments(Long jobId) {
		executeInTransaction(em -> {
			em.find(ClimateJob.class, jobId).setIncludeEnvironments(true);
		});
	}

	private void finish(Long jobId) {
		executeInTransaction(em -> {
			em.find(ClimateJob.class, jobId).setStatus(ClimateJobStatus.DONE);
		});
	}

	private void fill(Long locationId, LocalDate from, LocalDate to) {
		executeInTransaction(em -> {
			ClimateLocation location = em.find(ClimateLocation.class, locationId);
			for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
				ClimateObservation observation = new ClimateObservation();
				observation.setClimateLocation(location);
				observation.setObservationDate(ClimateCalendar.toDate(day));
				observation.setSource(ClimateDataSource.OPENWEATHER);
				observation.setRainfall(5.0);
				observation.setTemperatureMean(26.0);
				observation.setRelativeHumidity(80.0);
				observation.setHumidityMeasure(HumidityMeasure.NOON_READING);
				em.persist(observation);
			}
		});
	}
}
