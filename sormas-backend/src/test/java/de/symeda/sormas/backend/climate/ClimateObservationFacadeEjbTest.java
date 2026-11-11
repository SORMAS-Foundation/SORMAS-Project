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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.ClimateImportRowResult;
import de.symeda.sormas.api.climate.ClimateImportRowResult.Status;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationFacade;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateObservationFacadeEjb.ClimateObservationFacadeEjbLocal;
import de.symeda.sormas.backend.infrastructure.district.District;

class ClimateObservationFacadeEjbTest extends AbstractBeanTest {

	private static final LocalDate DAY = LocalDate.now().minusDays(30);

	private TestDataCreator.RDCF rdcf;
	private ClimateObservationFacade facade;

	@BeforeEach
	void setUpClimate() {
		createFeatureConfiguration(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF();
		facade = getBean(ClimateObservationFacadeEjbLocal.class);
	}

	@Test
	void testImportByDistrictUuidAndExternalId() {

		DistrictDto district = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		district.setExternalID("LGA-001");
		getDistrictFacade().save(district);

		ClimateObservationEntryDto byExternalId = entry(DAY.minusDays(1));
		byExternalId.setDistrictUuid(null);
		byExternalId.setDistrictExternalId("LGA-001");
		List<ClimateImportRowResult> results = importNow(entry(DAY), byExternalId);

		assertEquals(Status.SAVED, results.get(0).getStatus());
		assertEquals(Status.SAVED, results.get(1).getStatus());

		List<ClimateObservation> rows = getDistrictRows();
		assertEquals(2, rows.size(), "a district without a point imports to its location without coordinates");
		ClimateObservation row = rows.get(1);
		assertEquals(ClimateDataSource.IMPORT, row.getSource());
		assertEquals(18.2, row.getRainfall());
		assertEquals(HumidityMeasure.DAILY_MEAN, row.getHumidityMeasure());
		assertEquals(getCurrentUserService().getCurrentUser().getUserName(), row.getSourceDetail(), "no source detail: the user's name");
		assertEquals(getCurrentUserService().getCurrentUser().getUuid(), row.getReportingUser().getUuid());
	}

	@Test
	void testMeanIsFilledFromMinAndMax() {

		ClimateObservationEntryDto entry = entry(DAY);
		entry.setTemperatureMean(null);
		importNow(entry);

		assertEquals(26.7, getDistrictRows().get(0).getTemperatureMean());
	}

	@Test
	void testSourceOrderIsAppliedOnWrite() {

		Long location = getDistrictLocationId();
		createObservation(location, DAY, ClimateDataSource.OPENWEATHER, 1.0);
		createObservation(location, DAY.minusDays(1), ClimateDataSource.MANUAL, 2.0);
		createObservation(location, DAY.minusDays(2), ClimateDataSource.IMPORT, 3.0);

		List<ClimateImportRowResult> results = importNow(entry(DAY), entry(DAY.minusDays(1)), entry(DAY.minusDays(2)));

		assertEquals(Status.SAVED, results.get(0).getStatus(), "an import replaces OpenWeather");
		assertEquals(Status.SKIPPED, results.get(1).getStatus(), "a manual value is kept");
		assertEquals(Status.SKIPPED, results.get(2).getStatus(), "an earlier import is kept without overwrite");
		assertTrue(results.get(2).getMessage().contains("overwrite"));

		List<ClimateImportRowResult> overwritten =
			facade.importEntries(Collections.singletonList(entry(DAY.minusDays(2))), true, new Date(System.currentTimeMillis() + 1000));
		assertEquals(Status.SAVED, overwritten.get(0).getStatus());

		List<ClimateObservation> rows = getDistrictRows();
		assertEquals(18.2, rows.get(0).getRainfall());
		assertEquals(2.0, rows.get(1).getRainfall());
		assertEquals(ClimateDataSource.MANUAL, rows.get(1).getSource());
		assertEquals(18.2, rows.get(2).getRainfall());
	}

	@Test
	void testDuplicatesInOneImportAreRejected() {

		Date runStartedAt = new Date();
		List<ClimateImportRowResult> first = facade.importEntries(Arrays.asList(entry(DAY), entry(DAY)), true, runStartedAt);
		assertEquals(Status.SAVED, first.get(0).getStatus());
		assertEquals(Status.REJECTED, first.get(1).getStatus(), "twice in one batch");

		List<ClimateImportRowResult> laterBatch = facade.importEntries(Collections.singletonList(entry(DAY)), true, runStartedAt);
		assertEquals(Status.REJECTED, laterBatch.get(0).getStatus(), "again in a later batch of the same import");
	}

	@Test
	void testDistrictsSharingALocationKeepTheFirstValue() {

		District second = creator.createDistrict("Second district", getRegionService().getByUuid(rdcf.region.getUuid()));
		saveDistrictClimatePoint(rdcf.district.getUuid(), 6.60, 3.35);
		saveDistrictClimatePoint(second.getUuid(), 6.61, 3.34);

		ClimateObservationEntryDto secondEntry = entry(DAY);
		secondEntry.setDistrictUuid(second.getUuid());
		secondEntry.setRainfall(50.0);
		List<ClimateImportRowResult> results = importNow(entry(DAY), secondEntry);

		assertEquals(Status.SAVED, results.get(0).getStatus());
		assertEquals(Status.SKIPPED, results.get(1).getStatus());
		assertEquals(
			"Second district shares its climate location with " + rdcf.district.getCaption()
				+ ", which already has a value for this day from this import; the first value is kept.",
			results.get(1).getMessage());
		assertEquals(18.2, getDistrictRows().get(0).getRainfall());
	}

	@Test
	void testEnvironmentRowsNeedTheirOwnClimateLocation() {

		UserDto user = creator.createSurveillanceSupervisor(rdcf);
		EnvironmentDto withGps = createEnvironment(user, 6.60, 3.35);
		EnvironmentDto withoutGps = createEnvironment(user, 0.0, 0.0);

		ClimateObservationEntryDto site = entry(DAY);
		site.setDistrictUuid(null);
		site.setEnvironmentUuid(withGps.getUuid());
		ClimateObservationEntryDto noGps = entry(DAY);
		noGps.setDistrictUuid(null);
		noGps.setEnvironmentUuid(withoutGps.getUuid());
		ClimateObservationEntryDto unknown = entry(DAY);
		unknown.setDistrictUuid("XXXXXX-XXXXXX-XXXXXX-XXXXXXXX");

		List<ClimateImportRowResult> results = importNow(site, noGps, unknown);

		assertEquals(Status.SAVED, results.get(0).getStatus());
		assertEquals(Status.REJECTED, results.get(1).getStatus());
		assertTrue(results.get(1).getMessage().contains("no usable GPS"));
		assertEquals(Status.REJECTED, results.get(2).getStatus());
		assertTrue(results.get(2).getMessage().contains("XXXXXX-XXXXXX-XXXXXX-XXXXXXXX"));
	}

	@Test
	void testInvalidRowsAreRejectedWithTheReason() {

		ClimateObservationEntryDto tooWet = entry(DAY);
		tooWet.setRainfall(1200.0);
		ClimateObservationEntryDto today = entry(LocalDate.now());

		List<ClimateImportRowResult> results = importNow(tooWet, today, entry(DAY));

		assertEquals(Status.REJECTED, results.get(0).getStatus());
		assertEquals("Rainfall must be between 0 and 1000 mm a day.", results.get(0).getMessage());
		assertEquals(Status.REJECTED, results.get(1).getStatus());
		assertEquals(Status.SAVED, results.get(2).getStatus(), "valid rows in the same batch are still saved");
	}

	@Test
	void testAtMostOneThousandRowsPerCall() {

		List<ClimateObservationEntryDto> entries = new ArrayList<>();
		for (int i = 0; i < ClimateObservationFacade.MAX_IMPORT_ENTRIES + 1; i++) {
			entries.add(entry(DAY.minusDays(i)));
		}
		assertThrows(ValidationRuntimeException.class, () -> facade.importEntries(entries, true, new Date()));

		List<ClimateImportRowResult> results = facade.importEntries(entries.subList(0, ClimateObservationFacade.MAX_IMPORT_ENTRIES), true, new Date());
		assertTrue(results.stream().allMatch(r -> r.getStatus() == Status.SAVED));
		assertEquals(ClimateObservationFacade.MAX_IMPORT_ENTRIES, getDistrictRows().size());
	}

	@Test
	void testManualEntryWinsAndKeepsItsUser() {

		importNow(entry(DAY));

		ClimateObservationEntryDto correction = entry(DAY);
		correction.setRainfall(22.0);
		correction.setSourceDetail("NiMet correction");
		facade.saveManualEntry(correction);

		ClimateObservation row = getDistrictRows().get(0);
		assertEquals(ClimateDataSource.MANUAL, row.getSource());
		assertEquals(22.0, row.getRainfall());
		assertEquals("NiMet correction", row.getSourceDetail());

		assertEquals(Status.SKIPPED, facade.importEntries(Collections.singletonList(entry(DAY)), true, new Date()).get(0).getStatus());

		ClimateObservationEntryDto invalid = entry(DAY);
		invalid.setRelativeHumidity(120.0);
		assertThrows(ValidationRuntimeException.class, () -> facade.saveManualEntry(invalid));
	}

	@Test
	void testNothingIsSavedWhileClimateDataIsSwitchedOff() {

		// the rights (CLIMATE_IMPORT, CLIMATE_EDIT) are checked by the architecture test: bean tests do not run the rights interceptor
		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, false);
		assertThrows(ValidationRuntimeException.class, () -> facade.importEntries(Collections.singletonList(entry(DAY)), false, new Date()));
		assertThrows(ValidationRuntimeException.class, () -> facade.saveManualEntry(entry(DAY)));
	}

	private List<ClimateImportRowResult> importNow(ClimateObservationEntryDto... entries) {
		return facade.importEntries(Arrays.asList(entries), false, new Date());
	}

	private ClimateObservationEntryDto entry(LocalDate day) {
		ClimateObservationEntryDto entry = new ClimateObservationEntryDto();
		entry.setDistrictUuid(rdcf.district.getUuid());
		entry.setDate(day.toString());
		entry.setTemperatureMin(23.8);
		entry.setTemperatureMax(29.6);
		entry.setTemperatureMean(26.4);
		entry.setRainfall(18.2);
		entry.setRelativeHumidity(84.0);
		entry.setHumidityMeasure(HumidityMeasure.DAILY_MEAN);
		return entry;
	}

	private EnvironmentDto createEnvironment(UserDto user, double latitude, double longitude) {
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

	private Long getDistrictLocationId() {
		return executeInTransaction(em -> {
			District district = getDistrictService().getByUuid(rdcf.district.getUuid());
			if (district.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(district);
			}
			return district.getClimateLocation().getId();
		});
	}

	/**
	 * @return the district location's rows, newest day first
	 */
	private List<ClimateObservation> getDistrictRows() {
		Long location = getDistrictLocationId();
		return executeInTransaction(em -> {
			List<ClimateObservation> rows =
				new ArrayList<>(getBean(ClimateObservationService.class).getByLocation(em.find(ClimateLocation.class, location)));
			Collections.reverse(rows);
			rows.forEach(r -> {
				if (r.getReportingUser() != null) {
					r.getReportingUser().getUuid();
				}
			});
			return rows;
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
