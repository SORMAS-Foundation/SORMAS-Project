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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateDistrictDto;
import de.symeda.sormas.api.climate.ClimateHistoryCoverageDto;
import de.symeda.sormas.api.climate.ClimateObservationCriteria;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationExportDto;
import de.symeda.sormas.api.climate.ClimateObservationFacade;
import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.climate.ClimatePlaceType;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.region.RegionDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateJobFacadeEjb.ClimateJobFacadeEjbLocal;
import de.symeda.sormas.backend.climate.ClimateObservationFacadeEjb.ClimateObservationFacadeEjbLocal;
import de.symeda.sormas.backend.environment.Environment;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;

/**
 * The observation list, export, delete and history coverage behind the Climate data screen.
 */
class ClimateObservationListTest extends AbstractBeanTest {

	private static final LocalDate DAY = LocalDate.now().minusDays(30);

	private TestDataCreator.RDCF rdcf;
	private ClimateObservationFacade facade;
	private UserDto user;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		user = creator.createSurveillanceSupervisor(rdcf);
		facade = getBean(ClimateObservationFacadeEjbLocal.class);
	}

	@Test
	void testPlaceTypeRegionDistrictAndEnvironmentFilters() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		Long districtLocation = getDistrictLocationId(rdcf.district.getUuid());
		EnvironmentDto environment = createEnvironment("Well A", 6.80, 3.50);
		Long environmentLocation = getEnvironmentLocationId(environment.getUuid());

		Region otherRegion = creator.createRegion("Other region");
		District otherDistrict = creator.createDistrict("Other district", otherRegion);
		saveDistrictClimatePoint(otherDistrict.getUuid(), 9.00, 7.00);
		Long otherLocation = getDistrictLocationId(otherDistrict.getUuid());

		Long unlinkedLocation = createLocation(12.00, 8.00);

		createObservation(districtLocation, DAY, ClimateDataSource.OPENWEATHER, 1.0);
		createObservation(districtLocation, DAY.minusDays(1), ClimateDataSource.IMPORT, 2.0);
		createObservation(environmentLocation, DAY, ClimateDataSource.OPENWEATHER, 3.0);
		createObservation(otherLocation, DAY, ClimateDataSource.MANUAL, 4.0);
		createObservation(unlinkedLocation, DAY, ClimateDataSource.IMPORT, 5.0);

		assertEquals(Arrays.asList(1.0, 4.0, 2.0), rainfall(new ClimateObservationCriteria()), "districts by default, newest day first");
		assertEquals(Collections.singletonList(3.0), rainfall(new ClimateObservationCriteria().placeType(ClimatePlaceType.ENVIRONMENTS)));
		assertEquals(
			Arrays.asList(1.0, 3.0, 4.0, 5.0, 2.0),
			rainfall(new ClimateObservationCriteria().placeType(ClimatePlaceType.ALL)),
			"all places without a region also lists locations nothing links to");
		assertEquals(
			Arrays.asList(1.0, 3.0, 2.0),
			rainfall(new ClimateObservationCriteria().placeType(ClimatePlaceType.ALL).region(rdcf.region)),
			"a region's districts and environments");
		assertEquals(Arrays.asList(1.0, 2.0), rainfall(new ClimateObservationCriteria().district(rdcf.district)));
		assertEquals(
			Collections.singletonList(3.0),
			rainfall(new ClimateObservationCriteria().environment(environment.toReference())),
			"an environment selects its own location, whatever the place type");
		assertEquals(Collections.singletonList(2.0), rainfall(new ClimateObservationCriteria().source(ClimateDataSource.IMPORT)));
		assertEquals(
			Collections.singletonList(2.0),
			rainfall(
				new ClimateObservationCriteria()
					.dateBetween(ClimateCalendar.toDate(DAY.minusDays(5)), ClimateCalendar.toDate(DAY.minusDays(1)))));
		assertEquals(3, facade.count(new ClimateObservationCriteria()));
		assertEquals(5, facade.count(new ClimateObservationCriteria().placeType(ClimatePlaceType.ALL)));
	}

	@Test
	void testSortingAndPaging() {

		Long location = getDistrictLocationId(rdcf.district.getUuid());
		createObservation(location, DAY, ClimateDataSource.IMPORT, 5.0);
		createObservation(location, DAY.minusDays(1), ClimateDataSource.IMPORT, 1.0);
		createObservation(location, DAY.minusDays(2), ClimateDataSource.IMPORT, 3.0);

		List<ClimateObservationIndexDto> byRainfall = facade.getIndexList(
			new ClimateObservationCriteria(),
			0,
			100,
			Collections.singletonList(new SortProperty(ClimateObservationIndexDto.RAINFALL, true)));
		assertEquals(Arrays.asList(1.0, 3.0, 5.0), byRainfall.stream().map(ClimateObservationIndexDto::getRainfall).collect(Collectors.toList()));

		List<ClimateObservationIndexDto> secondPage = facade.getIndexList(new ClimateObservationCriteria(), 1, 1, null);
		assertEquals(1, secondPage.size());
		assertEquals(1.0, secondPage.get(0).getRainfall());
	}

	@Test
	void testRowsNameThePlacesSharingTheLocation() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		createEnvironment("Well B", 6.61, 3.34);
		createEnvironment("Well A", 6.60, 3.35);
		Long location = getDistrictLocationId(rdcf.district.getUuid());
		createObservation(location, DAY, ClimateDataSource.OPENWEATHER, 1.0);

		ClimateObservationIndexDto row = facade.getIndexList(new ClimateObservationCriteria(), 0, 10, null).get(0);
		assertEquals(Collections.singletonList(rdcf.district.getCaption()), row.getDistrictNames());
		assertEquals(Arrays.asList("Well A", "Well B"), row.getEnvironmentNames());
		assertTrue(row.isShared());
		assertEquals(6.60, row.getLatitude(), 0.0001);

		ClimateObservationExportDto export = facade.getExportList(new ClimateObservationCriteria(), 0, 10).get(0);
		assertEquals(rdcf.region.getCaption(), export.getRegions());
		assertEquals("Well A, Well B", export.getEnvironments());
		assertEquals(ClimateObservationExportDto.OPENWEATHER_CREDIT, export.getCredit(), "OpenWeather rows carry the credit");
	}

	@Test
	void testObservationOfADayWithoutARow() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		createEnvironment("Well A", 6.60, 3.35);

		ClimateObservationEntryDto key = new ClimateObservationEntryDto();
		key.setDistrictUuid(rdcf.district.getUuid());
		key.setDate(DAY.toString());
		ClimateObservationIndexDto observation = facade.getObservation(key);

		assertNull(observation.getUuid());
		assertNull(observation.getSource());
		assertEquals(ClimateCalendar.toDate(DAY), observation.getObservationDate());
		assertEquals(Collections.singletonList("Well A"), observation.getEnvironmentNames(), "the form names who else sees the value");

		ClimateObservationEntryDto entry = observation.toEntry();
		entry.setRainfall(12.0);
		facade.saveManualEntry(entry);

		ClimateObservationIndexDto saved = facade.getObservation(key);
		assertEquals(ClimateDataSource.MANUAL, saved.getSource());
		assertEquals(12.0, saved.getRainfall());
	}

	@Test
	void testOnlyManualAndImportedRowsCanBeDeleted() {

		Long location = getDistrictLocationId(rdcf.district.getUuid());
		createObservation(location, DAY, ClimateDataSource.OPENWEATHER, 1.0);
		createObservation(location, DAY.minusDays(1), ClimateDataSource.IMPORT, 2.0);
		createObservation(location, DAY.minusDays(2), ClimateDataSource.MANUAL, 3.0);
		List<ClimateObservationIndexDto> rows = facade.getIndexList(new ClimateObservationCriteria(), 0, 10, null);

		assertThrows(ValidationRuntimeException.class, () -> facade.deleteObservation(rows.get(0).getUuid()));
		facade.deleteObservation(rows.get(1).getUuid());
		facade.deleteObservation(rows.get(2).getUuid());

		assertEquals(Collections.singletonList(1.0), rainfall(new ClimateObservationCriteria()));
		assertThrows(ValidationRuntimeException.class, () -> facade.deleteObservation(rows.get(1).getUuid()), "already deleted");
	}

	@Test
	void testDistrictsWithoutAPoint() {

		District withPoint = creator.createDistrict("With point", getRegionService().getByUuid(rdcf.region.getUuid()));
		saveDistrictClimatePoint(withPoint.getUuid(), 6.60, 3.35);
		District archived = creator.createDistrict("Archived", getRegionService().getByUuid(rdcf.region.getUuid()));
		getDistrictFacade().archive(archived.getUuid());
		getDistrictLocationId(rdcf.district.getUuid());

		List<ClimateDistrictDto> withoutPoint = facade.getDistrictsWithoutPoint();
		assertEquals(Collections.singletonList(rdcf.district.getUuid()), withoutPoint.stream().map(d -> d.getDistrict().getUuid()).collect(Collectors.toList()));
		assertEquals(rdcf.region.getUuid(), withoutPoint.get(0).getRegion().getUuid());
	}

	@Test
	void testDistrictsOfOtherCountriesAreLeftOut() {

		// the test server's country is "nigeria"
		creator.createCountry("Nigeria", "NGA", "566");
		Country ghana = creator.createCountry("Ghana", "GHA", "288");
		Region abroad = creator.createRegion("Greater Accra");
		RegionDto abroadDto = getRegionFacade().getByUuid(abroad.getUuid());
		abroadDto.setCountry(new CountryReferenceDto(ghana.getUuid(), ghana.getIsoCode()));
		getRegionFacade().save(abroadDto);
		creator.createDistrict("Accra", getRegionService().getByUuid(abroad.getUuid()));

		List<ClimateDistrictDto> withoutPoint = facade.getDistrictsWithoutPoint();
		assertEquals(Collections.singletonList(rdcf.district.getUuid()), withoutPoint.stream().map(d -> d.getDistrict().getUuid()).collect(Collectors.toList()));
		assertEquals(1, getBean(ClimateJobFacadeEjbLocal.class).getSummary().getDistrictsWithoutPoint());
	}

	@Test
	void testHistoryCoverageGroupsYearsBySource() {

		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		Long location = getDistrictLocationId(rdcf.district.getUuid());
		int year = LocalDate.now().getYear();
		LocalDate firstDay = LocalDate.of(year - 2, 3, 1);
		createObservation(location, firstDay, ClimateDataSource.IMPORT, 1.0);
		createObservation(location, LocalDate.of(year - 1, 3, 1), ClimateDataSource.IMPORT, 1.0);
		createObservation(location, LocalDate.of(year - 1, 3, 2), ClimateDataSource.IMPORT, 1.0);
		createObservation(location, LocalDate.of(year, 1, 1), ClimateDataSource.OPENWEATHER, 1.0);
		createObservation(location, LocalDate.of(year, 1, 2), ClimateDataSource.MANUAL, 1.0);
		District empty = creator.createDistrict("Empty", getRegionService().getByUuid(rdcf.region.getUuid()));

		List<ClimateHistoryCoverageDto> coverage = facade.getHistoryCoverage(rdcf.region);
		assertEquals(2, coverage.size());
		ClimateHistoryCoverageDto district = coverage.stream().filter(c -> c.getDistrict().getUuid().equals(rdcf.district.getUuid())).findFirst().get();
		assertTrue(district.isHasPoint());
		assertEquals(firstDay, district.getHistoryFrom());
		assertEquals(5, district.getDaysHeld());

		List<ClimateHistoryCoverageDto.SourceSpan> spans = district.getSourceSpans();
		assertEquals(2, spans.size(), "two import years merge; the current year mixes two sources");
		assertEquals(year - 2, spans.get(0).getFromYear());
		assertEquals(year - 1, spans.get(0).getToYear());
		assertEquals(3, spans.get(0).getDaysBySource().get(ClimateDataSource.IMPORT));
		assertFalse(spans.get(1).isSingleSource());

		ClimateHistoryCoverageDto emptyDistrict = coverage.stream().filter(c -> c.getDistrict().getUuid().equals(empty.getUuid())).findFirst().get();
		assertNull(emptyDistrict.getHistoryFrom());
		assertEquals(0, emptyDistrict.getDaysHeld());
	}

	@Test
	void testEnvironmentsWithAClimateLocation() {

		createEnvironment("Well B", 6.60, 3.35);
		createEnvironment("No GPS", 0.0, 0.0);
		createEnvironment("Well A", 6.80, 3.50);

		assertEquals(
			Arrays.asList("Well A", "Well B"),
			facade.getEnvironmentsWithClimateLocation(null, rdcf.district).stream().map(e -> e.getCaption()).collect(Collectors.toList()),
			"only environments with usable GPS have a climate record of their own");
		assertEquals(2, facade.getEnvironmentsWithClimateLocation(rdcf.region, null).size());
		District other = creator.createDistrict("Other district", getRegionService().getByUuid(rdcf.region.getUuid()));
		assertTrue(facade.getEnvironmentsWithClimateLocation(null, getDistrictFacade().getReferenceByUuid(other.getUuid())).isEmpty());
	}

	private List<Double> rainfall(ClimateObservationCriteria criteria) {
		return facade.getIndexList(criteria, 0, 100, null).stream().map(ClimateObservationIndexDto::getRainfall).collect(Collectors.toList());
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

	private Long getDistrictLocationId(String districtUuid) {
		return executeInTransaction(em -> {
			District district = getDistrictService().getByUuid(districtUuid);
			if (district.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(district);
			}
			return district.getClimateLocation().getId();
		});
	}

	private Long getEnvironmentLocationId(String environmentUuid) {
		return executeInTransaction(em -> {
			Environment environment = getEnvironmentService().getByUuid(environmentUuid);
			return environment.getClimateLocation().getId();
		});
	}

	private Long createLocation(double latitude, double longitude) {
		return executeInTransaction(em -> {
			return getBean(ClimateLocationService.class).getOrCreate(latitude, longitude, 0.05).getId();
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
}
