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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.SiteClimateReadingDto;
import de.symeda.sormas.api.climate.SiteClimateReadingFacade;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.SiteClimateReadingFacadeEjb.SiteClimateReadingFacadeEjbLocal;
import de.symeda.sormas.backend.environment.Environment;

class SiteClimateReadingFacadeEjbTest extends AbstractBeanTest {

	private final LocalDate today = LocalDate.now();
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private UserDto otherNationalUser;
	private UserDto otherDistrictUser;
	private EnvironmentDto environment;
	private SiteClimateReadingFacade facade;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		createFeatureConfiguration(FeatureType.EDIT_ARCHIVED_ENTITIES, false);
		rdcf = creator.createRDCF();
		TestDataCreator.RDCF otherRdcf = creator.createRDCF("Region 2", "District 2", "Community 2", "Facility 2");
		// users are created before logging in: a national user may not create users
		user = creator.createNationalUser();
		otherNationalUser = creator.createUser(rdcf, creator.getUserRoleReference(DefaultUserRole.NATIONAL_USER));
		otherDistrictUser = creator.createUser(otherRdcf, creator.getUserRoleReference(DefaultUserRole.ENVIRONMENTAL_SURVEILLANCE_USER));
		loginWith(user);
		environment = creator.createEnvironment("Well A", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(6.80);
			e.getLocation().setLongitude(3.50);
		});
		facade = getBean(SiteClimateReadingFacadeEjbLocal.class);
	}

	@Test
	void testSaveAndListNewestFirstForThePeriod() {

		save(at(today.minusDays(3), 9), r -> r.setAirTemperature(27.5));
		SiteClimateReadingDto latest = save(at(today.minusDays(1), 14), r -> {
			r.setRainfall(12.0);
			r.setRainfallPeriodHours(24);
			r.setRelativeHumidity(81.0);
			r.setNotes("  after the storm  ");
		});
		save(at(today.minusDays(40), 9), r -> r.setAirTemperature(25.0));

		List<SiteClimateReadingDto> readings = facade.getReadings(environment.toReference(), today.minusDays(30), today);

		assertEquals(2, readings.size());
		SiteClimateReadingDto first = readings.get(0);
		assertEquals(latest.getUuid(), first.getUuid());
		assertEquals(12.0, first.getRainfall());
		assertEquals(24, first.getRainfallPeriodHours());
		assertEquals("after the storm", first.getNotes());
		assertEquals(user.getUuid(), first.getReportingUser().getUuid());
		assertEquals(27.5, readings.get(1).getAirTemperature());
	}

	@Test
	void testEditKeepsTheReportingUserAndDropsThePeriodWithoutRainfall() {

		SiteClimateReadingDto reading = save(at(today.minusDays(1), 8), r -> {
			r.setRainfall(3.0);
			r.setRainfallPeriodHours(6);
		});

		loginWith(otherNationalUser);
		reading.setRainfall(null);
		reading.setAirTemperature(24.0);
		SiteClimateReadingDto saved = facade.saveReading(reading);

		assertNull(saved.getRainfallPeriodHours());
		assertEquals(24.0, saved.getAirTemperature());
		assertEquals(user.getUuid(), saved.getReportingUser().getUuid());
		assertEquals(1, facade.getReadings(environment.toReference(), today.minusDays(1), today).size());
	}

	@Test
	void testValidation() {

		assertInvalid(at(today.plusDays(1), 8), r -> r.setAirTemperature(20.0));
		assertInvalid(at(today.minusDays(1), 8), r -> r.setNotes("no values"));
		assertInvalid(at(today.minusDays(1), 8), r -> r.setAirTemperature(61.0));
		assertInvalid(at(today.minusDays(1), 8), r -> r.setRainfall(5.0));
		assertInvalid(at(today.minusDays(1), 8), r -> {
			r.setRainfall(5.0);
			r.setRainfallPeriodHours(169);
		});
		assertInvalid(at(today.minusDays(1), 8), r -> r.setRainfall(-1.0));
		assertInvalid(at(today.minusDays(1), 8), r -> r.setRelativeHumidity(101.0));
		assertInvalid(null, r -> r.setAirTemperature(20.0));
	}

	@Test
	void testAReadingStaysWithItsEnvironment() {

		SiteClimateReadingDto reading = save(at(today.minusDays(1), 8), r -> r.setAirTemperature(20.0));
		EnvironmentDto other = creator.createEnvironment("Well B", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
		});
		reading.setEnvironment(other.toReference());

		assertThrows(ValidationRuntimeException.class, () -> facade.saveReading(reading));
	}

	@Test
	void testDeleteRemovesTheReading() {

		SiteClimateReadingDto reading = save(at(today.minusDays(1), 8), r -> r.setAirTemperature(20.0));
		facade.deleteReading(reading.getUuid());

		assertTrue(facade.getReadings(environment.toReference(), today.minusDays(7), today).isEmpty());
		assertThrows(ValidationRuntimeException.class, () -> facade.deleteReading(reading.getUuid()));
	}

	@Test
	void testArchivedEnvironmentIsNotEditable() {

		SiteClimateReadingDto reading = save(at(today.minusDays(1), 8), r -> r.setAirTemperature(20.0));
		executeInTransaction(em -> {
			getEnvironmentService().getByUuid(environment.getUuid()).setArchived(true);
		});

		reading.setAirTemperature(21.0);
		assertThrows(ValidationRuntimeException.class, () -> facade.saveReading(reading));
		assertThrows(ValidationRuntimeException.class, () -> facade.deleteReading(reading.getUuid()));
		assertEquals(1, facade.getReadings(environment.toReference(), today.minusDays(7), today).size(), "still listed");
	}

	@Test
	void testEnvironmentOutsideTheJurisdictionIsNotEditable() {

		loginWith(otherDistrictUser);

		SiteClimateReadingDto reading = SiteClimateReadingDto.build(environment.toReference());
		reading.setReadingDateTime(at(today.minusDays(1), 8));
		reading.setAirTemperature(20.0);

		assertThrows(ValidationRuntimeException.class, () -> facade.saveReading(reading));
	}

	@Test
	void testClimateSwitchedOff() {

		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, false);
		SiteClimateReadingDto reading = SiteClimateReadingDto.build(environment.toReference());
		reading.setReadingDateTime(at(today.minusDays(1), 8));
		reading.setAirTemperature(20.0);

		assertThrows(ValidationRuntimeException.class, () -> facade.saveReading(reading));
	}

	@Test
	void testPermanentEnvironmentDeleteRemovesItsReadings() {

		save(at(today.minusDays(1), 8), r -> r.setAirTemperature(20.0));
		executeInTransaction(em -> {
			Environment entity = getEnvironmentService().getByUuid(environment.getUuid());
			getEnvironmentService().deletePermanent(entity);
		});

		Long left = executeInTransaction(
			em -> {
				return em.createQuery("SELECT COUNT(r) FROM " + SiteClimateReading.TABLE_NAME + " r", Long.class).getSingleResult();
			});
		assertEquals(0L, left);
	}

	private SiteClimateReadingDto save(Date time, Consumer<SiteClimateReadingDto> values) {
		SiteClimateReadingDto reading = SiteClimateReadingDto.build(environment.toReference());
		reading.setReadingDateTime(time);
		values.accept(reading);
		return facade.saveReading(reading);
	}

	private void assertInvalid(Date time, Consumer<SiteClimateReadingDto> values) {
		assertThrows(ValidationRuntimeException.class, () -> save(time, values));
	}

	private static Date at(LocalDate day, int hour) {
		return Date.from(LocalDateTime.of(day, java.time.LocalTime.of(hour, 0)).atZone(ZoneId.systemDefault()).toInstant());
	}
}
