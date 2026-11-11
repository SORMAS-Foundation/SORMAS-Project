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

package de.symeda.sormas.ui.dashboard.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateLinkingService;
import de.symeda.sormas.backend.climate.ClimateLocation;
import de.symeda.sormas.backend.climate.ClimateObservation;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictService;
import de.symeda.sormas.backend.infrastructure.region.RegionService;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.climate.ClimateCaptions;

/**
 * The Climate dashboard export's columns and lines (decision 128), written in the UI from the backend's rows.
 */
class ClimateDashboardExportLinesTest extends AbstractUiBeanTest {

	private TestDataCreator.RDCF rdcf;
	private DistrictReferenceDto liberia;
	private TestDataCreator.RDCF liberiaPlace;
	private UserDto user;
	private EpiWeek lastWeek;

	@BeforeEach
	void setUpClimate() {
		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF("Guanacaste", "Bagaces", "Community", "Facility");
		de.symeda.sormas.backend.infrastructure.region.Region region = getBean(RegionService.class).getByUuid(rdcf.region.getUuid());
		District liberiaDistrict = creator.createDistrict("Liberia", region);
		de.symeda.sormas.backend.infrastructure.community.Community community = creator.createCommunity("Cañas Dulces", liberiaDistrict);
		de.symeda.sormas.backend.infrastructure.facility.Facility facility =
			creator.createFacility("Hospital Liberia", region, liberiaDistrict, community);
		liberia = DistrictFacadeEjb.toReferenceDto(liberiaDistrict);
		liberiaPlace = new TestDataCreator.RDCF(new TestDataCreator.RDCFEntities(region, liberiaDistrict, community, facility));
		user = useNationalAdminLogin();
		lastWeek = DateHelper.getPreviousEpiWeek(new Date());
		ClimateDashboardLayoutTest.markClimateSensitive(getDiseaseConfigurationFacade(), Disease.DENGUE, Disease.CHOLERA);
	}

	@Test
	void testHeaderAndLines() {

		Long bagaces = districtLocation(rdcf.district, 10.52657, -84.90845);
		fillWeek(bagaces, lastWeek, 10.0);
		creator.createPopulationData(rdcf.region, rdcf.district, 200_000, new Date());
		creator.createPopulationData(rdcf.region, liberia, 50_000, new Date());
		createCase(rdcf.district, Disease.DENGUE);
		createCase(rdcf.district, Disease.DENGUE);
		createCase(liberia, Disease.DENGUE);
		createCase(liberia, Disease.CHOLERA);

		ClimateDashboardLayout layout = new ClimateDashboardLayout(UserDto.build(), JurisdictionLevel.NATION);
		layout.getRegionFilter().setValue(rdcf.region);
		layout.apply();
		layout.getDiseaseGroup().setValue(new java.util.HashSet<>(java.util.Arrays.asList(Disease.DENGUE, Disease.CHOLERA)));

		layout.getLagSlider().setValue(5d);

		// the export: the region's weeks, then each district's, with the lag left out (decision 128)
		List<de.symeda.sormas.api.climate.ClimateDashboardExportDto> exportRows = layout.getExportRows();
		assertEquals(3 * 52, exportRows.size());
		List<String> header = ClimateDashboardExport.header(layout.selectedDiseases());
		assertEquals(5 + 6 * layout.selectedDiseases().size() + 21 + 1, header.size());
		assertTrue(header.contains(Disease.DENGUE + " cases"), header.toString());
		assertTrue(header.contains("Rainfall same week last year (mm)"));
		de.symeda.sormas.api.climate.ClimateDashboardExportDto lastBagacesWeek =
			exportRows.stream().filter(r -> rdcf.district.equals(r.getDistrict()) && r.getClimate().getWeek().equals(lastWeek)).findFirst().get();
		List<String> line = ClimateDashboardExport.line(lastBagacesWeek, layout.selectedDiseases());
		assertEquals(header.size(), line.size());
		assertEquals("Bagaces", line.get(1));
		assertEquals("200000", line.get(4));
		assertEquals("70", line.get(header.indexOf("Rainfall (mm)")), "the week's own rainfall, not the shifted one");
		assertEquals("7", line.get(header.indexOf("Rainfall days with data (districts for a region)")));
		assertEquals("2", line.get(header.indexOf(Disease.DENGUE + " cases")));
		assertEquals("", line.get(header.size() - 1), "no OpenWeather days, no credit");
	}

	private void createCase(DistrictReferenceDto district, Disease disease) {
		TestDataCreator.RDCF place = district.equals(liberia) ? liberiaPlace : rdcf;
		creator.createCase(user.toReference(), place, c -> {
			c.setDisease(disease);
			c.setCaseClassification(CaseClassification.SUSPECT);
			c.setReportDate(DateHelper.getEpiWeekStart(lastWeek));
			c.getSymptoms().setOnsetDate(DateHelper.getEpiWeekStart(lastWeek));
		});
	}

	private Long districtLocation(DistrictReferenceDto districtRef, double latitude, double longitude) {
		DistrictDto district = getDistrictFacade().getByUuid(districtRef.getUuid());
		district.setClimateLatitude(latitude);
		district.setClimateLongitude(longitude);
		getDistrictFacade().save(district);
		return executeInTransaction(em -> {
			District entity = getBean(DistrictService.class).getByUuid(districtRef.getUuid());
			if (entity.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(entity);
			}
			return entity.getClimateLocation().getId();
		});
	}

	private void fillWeek(Long location, EpiWeek week, double dailyRainfall) {
		LocalDate monday = ClimateCaptions.toLocalDate(DateHelper.getEpiWeekStart(week));
		executeInTransaction(em -> {
			for (int i = 0; i < 7; i++) {
				ClimateObservation observation = new ClimateObservation();
				observation.setClimateLocation(em.find(ClimateLocation.class, location));
				observation.setObservationDate(ClimateCaptions.toDate(monday.plusDays(i)));
				observation.setSource(ClimateDataSource.IMPORT);
				observation.setRainfall(dailyRainfall);
				observation.setTemperatureMin(20.0);
				observation.setTemperatureMean(25.0);
				observation.setTemperatureMax(30.0);
				observation.setRelativeHumidity(70.0);
				observation.setHumidityMeasure(HumidityMeasure.DAILY_MEAN);
				em.persist(observation);
			}
		});
	}

}
