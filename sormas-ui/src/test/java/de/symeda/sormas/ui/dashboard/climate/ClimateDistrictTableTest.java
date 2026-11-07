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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import de.symeda.sormas.ui.dashboard.climate.ClimateDistrictTable.Row;

class ClimateDistrictTableTest extends AbstractUiBeanTest {

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
		de.symeda.sormas.backend.infrastructure.facility.Facility facility = creator.createFacility("Hospital Liberia", region, liberiaDistrict, community);
		liberia = DistrictFacadeEjb.toReferenceDto(liberiaDistrict);
		liberiaPlace = new TestDataCreator.RDCF(new TestDataCreator.RDCFEntities(region, liberiaDistrict, community, facility));
		user = useNationalAdminLogin();
		lastWeek = DateHelper.getPreviousEpiWeek(new Date());
	}

	@Test
	void testOneRowPerDistrictWithTheChosenDisease() {

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

		ClimateDistrictTable table = layout.getTable();
		assertTrue(table.isVisible(), "a region without a district has the table");
		assertEquals("Districts of Guanacaste", table.getHeading().getValue());
		List<Disease> offered = ((com.vaadin.data.provider.ListDataProvider<Disease>) table.getDiseaseSelect().getDataProvider()).getItems()
			.stream()
			.collect(java.util.stream.Collectors.toList());
		assertEquals(layout.selectedDiseases(), offered, "the dropdown offers the ticked diseases");
		table.getDiseaseSelect().setValue(Disease.DENGUE);

		List<Row> rows = table.getRows();
		assertEquals(2, rows.size());
		Row bagacesRow = rows.stream().filter(r -> r.getDistrict().equals(rdcf.district)).findFirst().get();
		Row liberiaRow = rows.stream().filter(r -> r.getDistrict().equals(liberia)).findFirst().get();
		assertEquals("1.0", bagacesRow.valueText(ClimateDashboardLayout.Measure.INCIDENCE), "2 cases per 200,000");
		assertEquals("2.0", liberiaRow.valueText(ClimateDashboardLayout.Measure.INCIDENCE), "1 case per 50,000");
		assertEquals(ClimateDistrictTable.DASH, bagacesRow.weeksAboveText(), "no usual range");
		assertEquals("Usual range not shown: too few years of data", bagacesRow.getNoUsualRange(), "the reason on hover");
		assertEquals(ClimateDistrictTable.DASH, bagacesRow.recentText());
		assertEquals("70 mm", bagacesRow.rainfallText());
		assertEquals("25.0 °C", bagacesRow.temperatureText());
		assertEquals(ClimateDistrictTable.DASH, liberiaRow.rainfallText(), "no climate data");
		assertEquals(ClimateDistrictTable.DASH, bagacesRow.unusualText(), "no normals yet");
		assertTrue(bagacesRow.getNoNormals().startsWith("Building climate history"), bagacesRow.getNoNormals());
		assertEquals("2%", bagacesRow.completenessText(), "7 of the 364 days");
		assertTrue(bagacesRow.getCompletenessDetail().startsWith("Rainfall 2% · Temperature 2% · Humidity 2%"), bagacesRow.getCompletenessDetail());

		table.getDiseaseSelect().setValue(Disease.CHOLERA);
		rows = table.getRows();
		assertEquals("0", rows.stream().filter(r -> r.getDistrict().equals(rdcf.district)).findFirst().get().valueText(ClimateDashboardLayout.Measure.COUNT));

		layout.getLagSlider().setValue(5d);
		assertEquals("70 mm", table.getRows().stream().filter(r -> r.getDistrict().equals(rdcf.district)).findFirst().get().rainfallText(), "not shifted");

		table.getOnSelect().accept(liberia);
		assertEquals(liberia, layout.getSelection().getDistrict(), "a row click shows that district");
		assertFalse(table.isVisible());
	}

	@Test
	void testDistrictUserHasNoTable() {

		UserDto districtUser = UserDto.build();
		districtUser.setRegion(rdcf.region);
		districtUser.setDistrict(rdcf.district);
		ClimateDashboardLayout layout = new ClimateDashboardLayout(districtUser, JurisdictionLevel.DISTRICT);

		assertFalse(layout.getTable().isVisible());
		layout.selectDistrict(liberia);
		assertEquals(rdcf.district, layout.getSelection().getDistrict(), "a fixed district cannot be changed from the table");
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
