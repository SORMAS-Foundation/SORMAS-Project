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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.climate.ClimateCaseCriteria;
import de.symeda.sormas.api.climate.ClimateCaseFacade;
import de.symeda.sormas.api.climate.ClimateDashboardExportDto;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.AccessDeniedException;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateCaseFacadeEjb.ClimateCaseFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;

/**
 * The Climate dashboard export (decision 128): one row per place and week, climate beside each disease's cases, never
 * shifted, and only with both the export and the dashboard right.
 */
class ClimateDashboardExportTest extends AbstractBeanTest {

	private final EpiWeek week = ClimateStatistics.epiWeekOf(LocalDate.now().minusDays(14));
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private UserDto officer;
	private ClimateCaseFacade facade;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		// users are created before logging in as a national user
		officer = creator.createSurveillanceOfficer(rdcf);
		executeInTransaction(em -> {
			getUserRoleService().getByUuid(officer.getUserRoles().iterator().next().getUuid()).getUserRights().add(UserRight.CLIMATE_EXPORT);
		});
		// district edits need the national admin
		user = useNationalAdminLogin();
		facade = getBean(ClimateCaseFacadeEjbLocal.class);
	}

	@Test
	void testRowsPerPlaceAndWeek() {

		Long location = districtLocation();
		LocalDate monday = ClimateStatistics.firstDay(week);
		executeInTransaction(em -> {
			for (int i = 0; i < 7; i++) {
				ClimateObservation observation = new ClimateObservation();
				observation.setClimateLocation(em.find(ClimateLocation.class, location));
				observation.setObservationDate(ClimateCalendar.toDate(monday.plusDays(i)));
				observation.setSource(ClimateDataSource.OPENWEATHER);
				observation.setRainfall(3.0);
				observation.setTemperatureMean(26.0);
				observation.setRelativeHumidity(80.0);
				observation.setHumidityMeasure(HumidityMeasure.NOON_READING);
				em.persist(observation);
			}
		});
		creator.createPopulationData(rdcf.region, rdcf.district, 100_000, new Date());
		creator.createCase(user.toReference(), rdcf, c -> {
			c.setDisease(Disease.DENGUE);
			c.setCaseClassification(CaseClassification.SUSPECT);
			c.setReportDate(ClimateCalendar.toDate(monday.plusDays(2)));
			c.getSymptoms().setOnsetDate(ClimateCalendar.toDate(monday.plusDays(2)));
		});

		EpiWeek previous = ClimateStatistics.epiWeekOf(monday.minusDays(1));
		List<ClimateDashboardExportDto> rows = facade.getDashboardExportList(
			new ClimateCaseCriteria().region(rdcf.region).weeks(previous, week).diseases(Collections.singletonList(Disease.DENGUE)));

		assertEquals(4, rows.size(), "two weeks for the region, then two for its district");
		ClimateDashboardExportDto regionRow = rows.get(1);
		assertNull(regionRow.getDistrict());
		assertEquals(week, regionRow.getClimate().getWeek());
		assertEquals(21.0, regionRow.getClimate().getRainfall().getValue(), 1e-9);
		assertEquals(1, regionRow.getCases().get(Disease.DENGUE).getCount());
		assertEquals(1.0, regionRow.getCases().get(Disease.DENGUE).getIncidence(), 1e-9);
		assertEquals(Integer.valueOf(100_000), regionRow.getPopulation());
		assertTrue(regionRow.isOpenWeatherData(), "the credit goes on rows with OpenWeather days");

		ClimateDashboardExportDto districtRow = rows.get(3);
		assertEquals(rdcf.district, districtRow.getDistrict());
		assertEquals(7, districtRow.getClimate().getRainfall().getDays());
		assertEquals(0, rows.get(2).getCases().get(Disease.DENGUE).getCount());
		assertNull(rows.get(2).getClimate().getRainfall().getValue(), "a week without data is a gap");

		List<ClimateDashboardExportDto> districtRows = facade.getDashboardExportList(
			new ClimateCaseCriteria().region(rdcf.region).district(rdcf.district).weeks(week, week).diseases(Collections.emptyList()));
		assertEquals(1, districtRows.size(), "a district alone has its own rows only");
		assertTrue(districtRows.get(0).getCases().isEmpty());
		assertNull(districtRows.get(0).getPopulation(), "no disease, no population");
	}

	@Test
	void testNeedsTheDashboardRight() {

		loginWith(officer);

		assertThrows(
			AccessDeniedException.class,
			() -> facade.getDashboardExportList(new ClimateCaseCriteria().region(rdcf.region).weeks(week, week)),
			"the export holds the dashboard's case counts");
	}

	private Long districtLocation() {
		DistrictDto district = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		district.setClimateLatitude(6.60);
		district.setClimateLongitude(3.35);
		getDistrictFacade().save(district);
		return executeInTransaction(em -> {
			District entity = getDistrictService().getByUuid(rdcf.district.getUuid());
			if (entity.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(entity);
			}
			return entity.getClimateLocation().getId();
		});
	}
}
