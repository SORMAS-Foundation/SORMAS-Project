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

package de.symeda.sormas.ui.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateLinkingService;
import de.symeda.sormas.backend.climate.ClimateLocation;
import de.symeda.sormas.backend.climate.ClimateObservation;
import de.symeda.sormas.backend.climate.ClimateObservationService;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.importer.ImportResultStatus;

class ClimateObservationImporterTest extends AbstractUiBeanTest {

	private static final String HEADER =
		"region,district,districtExternalId,date,temperatureMin,temperatureMax,temperatureMean,rainfall,relativeHumidity,humidityMeasure,sourceDetail";

	private TestDataCreator.RDCF rdcf;

	@BeforeEach
	void setUpClimate() {
		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF("Lagos", "Ikeja", "Community", "Facility");
		DistrictDto district = getDistrictFacade().getByUuid(rdcf.district.getUuid());
		district.setExternalID("LGA-IKJ");
		getDistrictFacade().save(district);
	}

	@Test
	void testRowsAreMatchedCheckedAndSaved() throws Exception {

		LocalDate day = LocalDate.now().minusDays(30);
		List<String> lines = new ArrayList<>();
		lines.add(HEADER);
		lines.add("Lagos,Ikeja,," + day + ",23.8,29.6,,18.2,84,DAILY_MEAN,NiMet Ikeja station");
		lines.add(",,LGA-IKJ," + day.minusDays(1) + ",24.1,30.2,26.9,0,79,daily_mean,NiMet Ikeja station");
		lines.add("Lagos,Nowhere,," + day.minusDays(2) + ",24,30,27,1,80,DAILY_MEAN,");
		lines.add("Lagos,Ikeja,," + day.minusDays(3) + ",24,30,27,1200,80,DAILY_MEAN,");
		lines.add("Lagos,Ikeja,," + day + ",22,28,25,5,80,DAILY_MEAN,");
		lines.add("Lagos,Ikeja,," + day.minusDays(4) + ",24,30,27,abc,80,DAILY_MEAN,");

		TestImporter importer = importer(lines, false);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());

		List<ClimateObservation> rows = getDistrictRows();
		assertEquals(2, rows.size());
		ClimateObservation first = rows.get(0);
		assertEquals(ClimateDataSource.IMPORT, first.getSource());
		assertEquals(26.7, first.getTemperatureMean(), "the mean is filled from min and max");
		assertEquals("NiMet Ikeja station", first.getSourceDetail());
		assertEquals(0.0, rows.get(1).getRainfall(), "matched by external ID; a dry day is a value");

		String errors = importer.errors.toString();
		assertEquals(5, errors.split("\n").length, "header and four rejected rows: " + errors);
		assertTrue(errors.contains("No single district matches Lagos / Nowhere"));
		assertTrue(errors.contains("Rainfall must be between 0 and 1000 mm a day."));
		assertTrue(errors.contains("more than once in this import"));
	}

	@Test
	void testOverwriteDecidesAboutEarlierImports() throws Exception {

		LocalDate day = LocalDate.now().minusDays(30);
		importer(List.of(HEADER, "Lagos,Ikeja,," + day + ",23.8,29.6,26.4,18.2,84,DAILY_MEAN,"), false).runImport();

		TestImporter skipping = importer(List.of(HEADER, "Lagos,Ikeja,," + day + ",23.8,29.6,26.4,40,84,DAILY_MEAN,"), false);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, skipping.runImport().getStatus());
		assertTrue(skipping.errors.toString().contains("overwrite"));
		assertEquals(18.2, getDistrictRows().get(0).getRainfall());

		TestImporter overwriting = importer(List.of(HEADER, "Lagos,Ikeja,," + day + ",23.8,29.6,26.4,40,84,DAILY_MEAN,"), true);
		assertEquals(ImportResultStatus.COMPLETED, overwriting.runImport().getStatus());
		assertEquals(40.0, getDistrictRows().get(0).getRainfall());
	}

	@Test
	void testLargeFilesAreSavedInBatches() throws Exception {

		int days = ClimateObservationImporter.BATCH_SIZE * 2 + 3;
		LocalDate yesterday = LocalDate.now().minusDays(1);
		List<String> lines = new ArrayList<>();
		lines.add(HEADER);
		for (int i = 0; i < days; i++) {
			lines.add("Lagos,Ikeja,," + yesterday.minusDays(i) + ",23.8,29.6,26.4,5,84,DAILY_MEAN,");
		}

		assertEquals(ImportResultStatus.COMPLETED, importer(lines, false).runImport().getStatus());
		assertEquals(days, getDistrictRows().size(), "the last, partial batch is saved too");
	}

	private TestImporter importer(List<String> lines, boolean overwrite) throws IOException {
		Path file = Files.createTempFile("climate_import_test", ".csv");
		Files.write(file, lines, StandardCharsets.UTF_8);
		return new TestImporter(file.toFile(), overwrite);
	}

	/**
	 * @return the district location's rows, newest day first
	 */
	private List<ClimateObservation> getDistrictRows() {
		return executeInTransaction(em -> {
			District district = getDistrictService().getByUuid(rdcf.district.getUuid());
			if (district.getClimateLocation() == null) {
				getBean(ClimateLinkingService.class).linkDistrict(district);
			}
			List<ClimateObservation> rows = new ArrayList<>(
				getBean(ClimateObservationService.class).getByLocation(em.find(ClimateLocation.class, district.getClimateLocation().getId())));
			java.util.Collections.reverse(rows);
			return rows;
		});
	}

	private class TestImporter extends ClimateObservationImporter {

		private final StringWriter errors = new StringWriter();

		TestImporter(File file, boolean overwrite) throws IOException {
			super(file, getUserFacade().getCurrentUser(), ValueSeparator.COMMA, overwrite);
		}

		@Override
		protected Writer createErrorReportWriter() {
			return errors;
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}
}
